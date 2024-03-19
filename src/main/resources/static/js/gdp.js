const ctx = document.getElementById('myChart');

const parts = window.location.href.split('/');
const projectId = parts[parts.length - 2];
const after = parts[parts.length - 1];

if (projectId && after) {
    fetch("/e/" + projectId + "/" + after).then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    }).then(data => {
        const config = {
            type: 'radar',
            data: data,
            options: {
                elements: {
                    line: {
                        borderWidth: 1
                    }
                }
            },
        };

        // render
        new Chart(ctx, config);
    }).catch(error => {
        console.error('Ошибка при выполнении запроса:', error);
    });
} else {
    console.log('После последнего слеша в URL ничего не найдено.');
}
