document.addEventListener('DOMContentLoaded', function() {
    // Элементы модального окна
    const filterModal = document.getElementById('filterModal');
    const filterForm = document.getElementById('filterForm');
    const clearFiltersBtn = document.getElementById('clearFilters');
    const openFilterButtons = document.querySelectorAll('[data-target="#filterModal"]');

    // Открытие модального окна
    openFilterButtons.forEach(button => {
        button.addEventListener('click', function() {
            filterModal.style.display = 'flex';

            // Восстановление сохраненных значений
            const savedMinPrice = localStorage.getItem('minPrice');
            const savedMaxPrice = localStorage.getItem('maxPrice');

            document.getElementById('minPrice').value = savedMinPrice || document.getElementById('minPrice').getAttribute('value') || '';
            document.getElementById('maxPrice').value = savedMaxPrice || document.getElementById('maxPrice').getAttribute('value') || '';
        });
    });

    document.getElementById('clearSorting')?.addEventListener('click', function() {
        const url = new URL(window.location.href);
        url.searchParams.delete('sortBy');
        window.location.href = url.toString();
        $('#sortModal').modal('hide'); // Закрываем модальное окно
    });

    document.getElementById('clearFilters')?.addEventListener('click', function() {
        // Создаем URL без параметров фильтрации
        const url = new URL(window.location.href);
        url.searchParams.delete('minPrice');
        url.searchParams.delete('maxPrice');

        // Закрываем модальное окно
        $('#filterModal').modal('hide');

        // Переходим по новому URL (сброс фильтров)
        window.location.href = url.toString();
    });
    // Закрытие модального окна при клике вне его области
    filterModal.addEventListener('click', function(e) {
        if (e.target === filterModal) {
            filterModal.style.display = 'none';
        }
    });

    // Сохранение параметров при отправке формы
    if (filterForm) {
        filterForm.addEventListener('submit', function() {
            localStorage.setItem('minPrice', document.getElementById('minPrice').value);
            localStorage.setItem('maxPrice', document.getElementById('maxPrice').value);
            filterModal.style.display = 'none';
        });
    }

    // Очистка фильтров
    if (clearFiltersBtn) {
        clearFiltersBtn.addEventListener('click', function() {
            localStorage.removeItem('minPrice');
            localStorage.removeItem('maxPrice');
            document.getElementById('minPrice').value = '';
            document.getElementById('maxPrice').value = '';
        });
    }
});