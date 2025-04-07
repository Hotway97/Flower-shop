$(document).ready(function() {
    // Функция для обновления видимости кнопки
    function updateCreateButtonVisibility(hasChat) {
        $('#createChatBtn').toggle(!hasChat);
    }

    // Получение списка чатов
    function fetchChats() {
        $.ajax({
            url: '/chats',
            method: 'GET',
            dataType: 'json',
            success: function(chats) {
                $('#chatList').empty();

                if (chats?.id) {
                    $('#chatList').append(`
                    <li class="chatItem selected" data-id="${chats.id}">
                        ${chats.chatName}
                        <button class="clearHistoryBtn">Очистить историю</button>
                    </li>
                `);

                    // Сразу загружаем сообщения
                    loadChatMessages(chats.id);
                    updateCreateButtonVisibility(true);
                } else {
                    $('#chatList').append('<li>Нет доступных чатов</li>');
                    updateCreateButtonVisibility(false);
                }
            },
            error: function(xhr) {
                $('#chatList').html('<li>Ошибка загрузки</li>');
            }
        });
    }


    // Создание нового чата
    $('#createChatBtn').on('click', function() {
        $.ajax({
            url: '/chats',
            method: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({ chatName: "Новый чат" }),
            success: function(chat) {
                fetchChats();
            },
            error: function(xhr) {
                alert('Ошибка: ' + xhr.responseJSON?.error);
            }
        });
    });

    // Очистка истории сообщений
    $(document).on('click', '.clearHistoryBtn', function() {
        const chatId = $(this).parent().data('id');
        if (confirm("Очистить историю сообщений?")) {
            $.ajax({
                url: `/chats/${chatId}/messages`,
                method: 'DELETE',
                success: function() {
                    $('#response').empty();
                    alert('История сообщений очищена');
                },
                error: function(xhr) {
                    alert('Ошибка: ' + xhr.responseJSON?.error);
                }
            });
        }
    });

    // Переключение между чатами
    $(document).on('click', '.chatItem', function() {
        $('.chatItem').removeClass('selected');
        $(this).addClass('selected');
        const chatId = $(this).data('id');
        loadChatMessages(chatId);
    });

    // Загрузка сообщений чата
    function loadChatMessages(chatId) {
        $('#response').empty();
        $.ajax({
            url: `/chats/${chatId}/messages`,
            method: 'GET',
            success: function(messages) {
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
            error: function(xhr) {
                handleChatError(xhr.status);
            }
        });
    }

    // Обработка ошибок
    function handleChatError(status) {
        const errors = {
            404: 'Чат не найден',
            403: 'Доступ запрещен',
            500: 'Ошибка сервера'
        };
        alert(errors[status] || 'Неизвестная ошибка');
    }

    // Отправка сообщений
    $('#chatForm').on('submit', function(e) {
        e.preventDefault();
        const chatId = $('.chatItem.selected').data('id');
        const message = $('#userInput').val().trim();

        if (!chatId || !message) return;

        sendMessageToAI(chatId, message);
        $('#userInput').val('');
    });

    // Отправка сообщения ИИ
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
            reader.read().then(({done, value}) => {
                if (done) return;
                aiMessageEl.append(decoder.decode(value));
                read();
            });
        }
        read();
    }

    // Скроллим вниз, когда сообщение приходит
    function scrollToBottom() {
        const container = document.getElementById("response");
        container.scrollTop = container.scrollHeight;
    }

    // Обработка ошибок ИИ
    function handleAIError(error) {
        console.error(error);
        $('#response .loading').replaceWith(`
            <div class="message ai-message error">
                <strong>Ошибка:</strong> ${error.message}
            </div>
        `);
    }

    // Инициализация
    $('#sidePanelToggle').on('click', () => $('#sidePanel').toggleClass('open'));
    fetchChats();
});