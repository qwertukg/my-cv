const ctx = document.getElementById('myChart');





// // Получаем текущий URL
// const currentUrl = window.location.href;
//
// // Находим последний слеш в URL и получаем подстроку после него
// const lastSlashIndex = currentUrl.lastIndexOf('/');
// const pathAfterLastSlash = currentUrl.substring(lastSlashIndex + 1);



// Получаем текущий URL
const currentUrl = window.location.href;

const parts = currentUrl.split('/');
const projectId = parts[parts.length - 2];
const after = parts[parts.length - 1];

// Проверяем, существует ли путь после последнего слеша
if (projectId && after) {
    // Выполняем GET-запрос к полученному пути
    fetch("/e/" + projectId + "/" + after)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const config = {
                type: 'radar',
                data: data,
                options: {
                    elements: {
                        line: {
                            borderWidth: 3
                        }
                    }
                },
            };

            // render
            new Chart(ctx, config);
        })
        .catch(error => {
            console.error('Ошибка при выполнении запроса:', error);
        });
} else {
    console.log('После последнего слеша в URL ничего не найдено.');
}


// const data = {
//     labels: [
//         'Eating',
//         'Drinking',
//         'Sleeping',
//         'Designing',
//         'Coding',
//         'Cycling',
//         'Running'
//     ],
//     datasets: [{
//         label: 'My First Dataset',
//         data: [65, 59, 90, 81, 56, 55, 40]
//     }, {
//         label: 'My Second Dataset',
//         data: [28, 48, 40, 19, 96, 27, 100]
//     }]
// };

