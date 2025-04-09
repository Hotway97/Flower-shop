// document.querySelectorAll('.custom-file-input').forEach(input => {
//     input.addEventListener('change', function() {
//         this.nextElementSibling.innerText = this.files[0]?.name || 'Файл не выбран';
//     });
// });
document.querySelectorAll('.custom-file-input').forEach(input => {
    input.addEventListener('change', function (e) {
        const fileName = e.target.files[0]?.name || 'Файл не выбран';
        const label = e.target.nextElementSibling;
        if (label) {
            label.textContent = fileName;
        }
    });
});