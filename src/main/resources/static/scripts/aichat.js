$(document).ready(function () {
    let activeChatId = null;

    // Получение или создание чата
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

    // Создание нового чата
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

    // Загрузка сообщений
    function loadChatMessages(chatId) {
        $('#response').empty();
        $.ajax({
            url: `/chats/${chatId}/messages`,
            method: 'GET',
            success: function (messages) {
                messages.forEach(msg => {
                    const messageClass = msg.isAiResponse ? "ai-message" : "user-message";
                    const sender = msg.isAiResponse ? 'ИИ' : 'Вы';
                    $('#response').append(`
                        <div class="message ${messageClass}">
                            <strong>${sender}:</strong> ${msg.content}
                        </div>
                    `);
                });
            },
            error: function (xhr) {
                handleChatError(xhr.status);
            }
        });
    }

    // Отправка сообщений
    $('#chatForm').on('submit', function (e) {
        e.preventDefault();
        const message = $('#userInput').val().trim();
        if (!activeChatId || !message) return;
        sendMessageToAI(activeChatId, message);
        $('#userInput').val('');
    });

    // Отправка запроса ИИ
    function sendMessageToAI(chatId, message) {
        $('#response').append(`
            <div class="message user-message">
                <strong>Вы:</strong> ${message}
            </div>
            <div class="message loading">ИИ печатает...</div>
        `);

        fetch(`/ollama?chatId=${chatId}&input=${encodeURIComponent(message)}`, {
            method: 'POST'
        })
            .then(response => processAIResponse(response))
            .catch(error => handleAIError(error));
    }

    // Обработка ответа ИИ
    function processAIResponse(response) {
        if (!response.ok) throw new Error('Ошибка сети');

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        const aiMessageEl = $('<div class="message ai-message"><strong>ИИ:</strong> </div>');

        $('#response .loading').remove();
        $('#response').append(aiMessageEl);

        function read() {
            reader.read().then(({ done, value }) => {
                if (done) return;
                aiMessageEl.append(decoder.decode(value));
                read();
            });
        }

        read();
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

    // Обработка ошибок
    function handleChatError(status) {
        const errors = {
            404: 'Чат не найден',
            403: 'Доступ запрещен',
            500: 'Ошибка сервера'
        };
        alert(errors[status] || 'Неизвестная ошибка');
    }

    // Загрузка при старте
    fetchOrCreateChat();
});
