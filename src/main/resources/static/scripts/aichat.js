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
                        $('#response').append(createAiMessage(msg.content));
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
    function createAiMessage(content) {
        return $(`
            <div class="message ai-message">
                <strong>Букетик:</strong> 
                <span class="ai-text">${content}</span>
            </div>
        `);
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

        fetch(`/deepseek?chatId=${chatId}&input=${encodeURIComponent(message)}`, {
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

        function read() {
            reader.read().then(({ done, value }) => {
                if (done) return;

                const textChunk = decoder.decode(value);
                aiMessage.find('.ai-text').append(document.createTextNode(textChunk));
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