$(document).ready(function () {
    let activeChatId = null;

    // ======================== Чат-функции ========================
    function fetchOrCreateChat() {
        $.ajax({
            url: '/chats',
            method: 'GET',
            dataType: 'json',
            success: function (chat) {
                if (chat?.id) {
                    activeChatId = chat.id;
                    loadChatMessages(chat.id);
                } else {
                    createNewChat();
                }
            },
            error: function () {
                alert("Ошибка при загрузке чата");
            }
        });
    }

    function createNewChat() {
        $.ajax({
            url: '/chats',
            method: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({ chatName: "Новый чат" }),
            success: function (chat) {
                activeChatId = chat.id;
                loadChatMessages(chat.id);
            },
            error: function () {
                alert("Ошибка при создании чата");
            }
        });
    }

    function loadChatMessages(chatId) {
        $('#response').empty();
        $.ajax({
            url: `/chats/${chatId}/messages`,
            method: 'GET',
            success: function (messages) {
                messages.forEach(msg => {
                    if (msg.isAiResponse) {
                        $('#response').append(createAiMessage(msg.content, true));
                    } else {
                        $('#response').append(`
                            <div class="message user-message">
                                <strong>Вы:</strong> ${msg.content}
                            </div>
                        `);
                    }
                });
            },
            error: function (xhr) {
                handleChatError(xhr.status);
            }
        });
    }

    // ======================== Обработка сообщений ========================
    function createAiMessage(content, isOldMessage = false) {
        const aiMessageEl = $(`
        <div class="message ai-message">
            <strong>Букетик:</strong>
            <div class="think-container">
                <button class="think-toggle">📝 Показать рассуждения</button>
                <div class="think-content" style="display:none"></div>
            </div>
            <div class="ai-main-text"></div>
        </div>
    `);

        const mainTextEl = aiMessageEl.find('.ai-main-text');
        const thinkContent = aiMessageEl.find('.think-content');
        const thinkToggle = aiMessageEl.find('.think-toggle');

        // Парсинг контента
        let inThinkBlock = false;
        let remainingContent = content;
        let hasThinkContent = false;

        while (remainingContent.length > 0) {
            if (!inThinkBlock) {
                const thinkStart = remainingContent.indexOf('<think>');
                if (thinkStart >= 0) {
                    mainTextEl.append(remainingContent.substring(0, thinkStart));
                    inThinkBlock = true;
                    remainingContent = remainingContent.substring(thinkStart + 7);
                    hasThinkContent = true;
                } else {
                    mainTextEl.append(remainingContent);
                    remainingContent = '';
                }
            } else {
                const thinkEnd = remainingContent.indexOf('</think>');
                if (thinkEnd >= 0) {
                    thinkContent.append(remainingContent.substring(0, thinkEnd));
                    remainingContent = remainingContent.substring(thinkEnd + 8);
                    inThinkBlock = false;
                } else {
                    thinkContent.append(remainingContent);
                    remainingContent = '';
                }
            }
        }

        // Настройка поведения кнопки
        thinkToggle.on('click', function() {
            thinkContent.slideToggle();
            $(this).text(
                thinkContent.is(':visible')
                    ? '📝 Скрыть рассуждения'
                    : '📝 Показать рассуждения'
            );
        });

        // Скрываем контейнер, если нет рассуждений
        if (!hasThinkContent) {
            aiMessageEl.find('.think-container').hide();
        }

        return aiMessageEl;
    }

    // ======================== Отправка сообщений ========================
    $('#chatForm').on('submit', function (e) {
        e.preventDefault();
        const message = $('#userInput').val().trim();
        if (!activeChatId || !message) return;
        sendMessageToAI(activeChatId, message);
        $('#userInput').val('');
    });

    function sendMessageToAI(chatId, message) {
        $('#response').append(`
            <div class="message user-message">
                <strong>Вы:</strong> ${message}
            </div>
            <div class="message loading">Букетик думает...</div>
        `);

        fetch(`/ollama?chatId=${chatId}&input=${encodeURIComponent(message)}`, {
            method: 'POST'
        })
            .then(response => processAIResponse(response))
            .catch(error => handleAIError(error));
    }

    function processAIResponse(response) {
        if (!response.ok) throw new Error('Ошибка сети');

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        const aiMessage = createAiMessage('');

        $('#response .loading').remove();
        $('#response').append(aiMessage);

        let inThinkBlock = false;
        let currentBuffer = '';
        let hasThinkContent = false;

        function processChunk(text) {
            currentBuffer += text;

            while (true) {
                if (!inThinkBlock) {
                    const thinkStart = currentBuffer.indexOf('<think>');
                    if (thinkStart >= 0) {
                        aiMessage.find('.ai-main-text').append(currentBuffer.substring(0, thinkStart));
                        currentBuffer = currentBuffer.substring(thinkStart + 7);
                        inThinkBlock = true;
                        hasThinkContent = true;
                        aiMessage.find('.think-container').show();
                    } else break;
                } else {
                    const thinkEnd = currentBuffer.indexOf('</think>');
                    if (thinkEnd >= 0) {
                        aiMessage.find('.think-content').append(currentBuffer.substring(0, thinkEnd));
                        currentBuffer = currentBuffer.substring(thinkEnd + 8);
                        inThinkBlock = false;
                    } else break;
                }
            }

            if (!inThinkBlock && currentBuffer.length > 0) {
                aiMessage.find('.ai-main-text').append(currentBuffer);
                currentBuffer = '';
            }
        }

        function read() {
            reader.read().then(({ done, value }) => {
                if (done) {
                    if (currentBuffer.length > 0) processChunk('');
                    return;
                }
                processChunk(decoder.decode(value));
                read();
            });
        }

        read();
    }

    // ======================== Вспомогательные функции ========================
    function handleAIError(error) {
        console.error('AI Error:', error);
        $('#response .loading').remove();
        $('#response').append(`
            <div class="message error">
                <strong>Ошибка:</strong> ${error.message}
            </div>
        `);
    }

    // Очистка истории
    $('#clearChatBtn').on('click', function () {
        if (!activeChatId) return;
        if (confirm("Очистить историю сообщений?")) {
            $.ajax({
                url: `/chats/${activeChatId}/messages`,
                method: 'DELETE',
                success: function () {
                    $('#response').empty();
                    alert('История сообщений очищена');
                },
                error: function (xhr) {
                    alert('Ошибка: ' + xhr.responseJSON?.error);
                }
            });
        }
    });

    function handleChatError(status) {
        const errors = {
            404: 'Чат не найден',
            403: 'Доступ запрещен',
            500: 'Ошибка сервера'
        };
        alert(errors[status] || 'Неизвестная ошибка');
    }

    // ======================== Инициализация ========================
    fetchOrCreateChat();
});