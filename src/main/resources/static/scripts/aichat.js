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
        const aiMessageEl = $('<div class="message ai-message"><strong>Букетик:</strong> </div>');
        const mainTextEl = $('<div class="ai-main-text"></div>');
        const thinkContainer = $('<div class="think-container"></div>');
        const thinkContent = $('<div class="think-content" style="display:none"></div>');

        let inThinkBlock = false;
        let remainingContent = content;

        // Парсинг контента
        while (remainingContent.length > 0) {
            if (!inThinkBlock) {
                const thinkStart = remainingContent.indexOf('<think>');
                if (thinkStart >= 0) {
                    mainTextEl.append(remainingContent.substring(0, thinkStart));
                    inThinkBlock = true;
                    remainingContent = remainingContent.substring(thinkStart + 7);
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

        // Добавляем контент рассуждений, но скрываем его
        thinkContainer.append(thinkContent);

        // Скрываем контейнер с рассуждениями, если он существует, но не добавляем кнопку
        if (thinkContent.length > 0) {
            thinkContainer.hide(); // скрываем контейнер, если есть рассуждения
        } else {
            thinkContainer.remove(); // если нет рассуждений, убираем контейнер вообще
        }

        aiMessageEl.append(thinkContainer).append(mainTextEl);

        // Возвращаем созданное сообщение без кнопки
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

        // Добавляем временный индикатор
        const thinkingIndicator = $('<div class="message loading">Букетик думает...</div>');
        $('#response').append(thinkingIndicator);

        let hasVisibleContent = false;
        let inThinkBlock = false;
        let currentBuffer = '';

        function processChunk(text) {
            currentBuffer += text;

            while (true) {
                if (!inThinkBlock) {
                    const thinkStart = currentBuffer.indexOf('<think>');
                    if (thinkStart >= 0) {
                        // Если есть текст перед <think>, показываем его
                        const visiblePart = currentBuffer.substring(0, thinkStart);
                        if (visiblePart.trim().length > 0) {
                            aiMessage.find('.ai-main-text').append(visiblePart);
                            hasVisibleContent = true;
                            thinkingIndicator.remove();
                        }
                        currentBuffer = currentBuffer.substring(thinkStart + 7);
                        inThinkBlock = true;
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
                hasVisibleContent = true;
                thinkingIndicator.remove();
                currentBuffer = '';
            }

            // Если основной текст пуст, но есть think-контент - сохраняем индикатор
            if (!hasVisibleContent && thinkingIndicator.length === 0) {
                $('#response').append(thinkingIndicator);
            }
        }

        function read() {
            reader.read().then(({ done, value }) => {
                if (done) {
                    thinkingIndicator.remove(); // Удаляем индикатор при завершении
                    if (currentBuffer.length > 0) processChunk('');
                    return;
                }
                processChunk(decoder.decode(value));
                read();
            });
        }

        $('#response .loading').remove();
        $('#response').append(aiMessage);
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