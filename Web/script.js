class PointChecker {
    constructor() {
        this.currentX = null;
        this.SERVER_URL = '/api';
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.drawGraph();
        this.loadResults();
    }

    setupEventListeners() {

    // === Кнопки X ===
    const xButtons = document.querySelectorAll('.x-btn');

    xButtons.forEach(btn => {
        btn.addEventListener('click', (e) => {
            this.selectXValue(e.target.value);
        });
    });

    // === Поля Y и R ===
    const yInput = document.getElementById('y');
    const rInput = document.getElementById('r');


    if (yInput) {
        yInput.addEventListener('input', (e) => {
            this.validateY(e.target.value);
        });
    }

    if (rInput) {
        // При изменении R — валидация и перерисовка графика
        rInput.addEventListener('input', (e) => {
            const value = this.parseNumber(e.target.value);
            const valid = this.validateR(e.target.value);

            if (valid && !isNaN(value)) {
                this.drawGraph(value);  // перерисовка зоны с новым R
            }
        });
    }

    // === Отправка формы ===
    const form = document.getElementById('pointForm');

    if (form) {
        form.addEventListener('submit', (e) => {
            e.preventDefault();
            this.submitForm();
        });
    }

    // === Очистка результатов ===
    const clearBtn = document.getElementById('clearBtn');

    if (clearBtn) {
        clearBtn.addEventListener('click', () => {
            this.clearResults();
        });
    }

    // === Клик по канвасу для добавления точки ===
    const canvas = document.getElementById('areaGraph');
    if (canvas) {
        canvas.addEventListener('click', (e) => {
            const rect = canvas.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;
            const size = canvas.width;
            const center = size / 2;
            const scale = size / 10;

            // преобразуем координаты из пикселей в реальные значения
            const rValue = this.parseNumber(rInput?.value);
            if (isNaN(rValue)) return;

            const realX = (x - center) / scale;
            const realY = (center - y) / scale;

            document.getElementById('y').value = realY.toFixed(2);
            this.selectXValue(realX.toFixed(2));
        });
    }
}


    selectXValue(value) {
        this.currentX = value;
        
        // Снимаем активный класс со всех кнопок
        document.querySelectorAll('.x-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        
        // Добавляем активный класс к выбранной кнопке
        const activeButton = Array.from(document.querySelectorAll('.x-btn'))
            .find(btn => btn.value === value);
        if (activeButton) {
            activeButton.classList.add('active');
        }
        
        // Устанавливаем значение в скрытое поле
        const xInput = document.getElementById('x');
        if (xInput) {
            xInput.value = value;
        }
    }

    validateY(value) {
        const errorElement = document.getElementById('y-error');
        if (!errorElement) {
            console.error('y-error element not found');
            return false;
        }

        const numValue = this.parseNumber(value);
        
        if (value === '') {
            errorElement.textContent = '';
            return false;
        }

        if (isNaN(numValue)) {
            errorElement.textContent = 'Y должен быть числом';
            return false;
        }

        if (numValue < -5 || numValue > 3) {
            errorElement.textContent = 'Y должен быть в диапазоне от -5 до 3';
            return false;
        }

        errorElement.textContent = '';
        return true;
    }

    validateR(value) {
        const errorElement = document.getElementById('r-error');
        if (!errorElement) {
            console.error('r-error element not found');
            return false;
        }

        const numValue = this.parseNumber(value);
        
        if (value === '') {
            errorElement.textContent = '';
            return false;
        }

        if (isNaN(numValue)) {
            errorElement.textContent = 'R должен быть числом';
            return false;
        }

        if (numValue < 2 || numValue > 5) {
            errorElement.textContent = 'R должен быть в диапазоне от 2 до 5';
            return false;
        }

        errorElement.textContent = '';
        return true;
    }

    parseNumber(value) {
        if (!value || value === '') return NaN;
        // Заменяем запятую на точку для корректного парсинга
        const normalizedValue = value.replace(',', '.');
        return parseFloat(normalizedValue);
    }

    validateForm() {
        const xInput = document.getElementById('x');
        const yInput = document.getElementById('y');
        const rInput = document.getElementById('r');

        if (!xInput || !yInput || !rInput) {
            alert('Форма не найдена');
            return false;
        }

        const x = xInput.value;
        const y = yInput.value;
        const r = rInput.value;

        if (!x) {
            alert('Выберите значение X');
            return false;
        }

        if (!y) {
            alert('Введите значение Y');
            return false;
        }

        if (!r) {
            alert('Введите значение R');
            return false;
        }

        const yValid = this.validateY(y);
        const rValid = this.validateR(r);

        if (!yValid || !rValid) {
            alert('Исправьте ошибки в форме');
            return false;
        }

        return true;
    }

    async submitForm() {
        if (!this.validateForm()) return;

        const xInput = document.getElementById('x');
        const yInput = document.getElementById('y');
        const rInput = document.getElementById('r');

        const formData = {
            x: xInput.value,
            y: yInput.value.replace(',', '.'),
            r: rInput.value.replace(',', '.')
        };

        this.showLoading(true);

        try {
            const response = await this.sendAjaxRequest(formData);

            this.handleResponse(response);
        } catch (error) {
            console.error('Request error:', error);
            alert('Произошла ошибка при отправке запроса: ' + error.message);
        } finally {
            this.showLoading(false);
        }
    }

    async sendAjaxRequest(data) {
        
        const response = await fetch(this.SERVER_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const responseData = await response.json();
        return responseData;
    }

    handleResponse(response) {
        if (response.success && response.data) {
            this.addResultToTable(response.data);
            this.drawPoint(response.data.x, response.data.y, response.data.result);
            this.saveResults();
        } else {
            alert('Ошибка сервера: ' + (response.error || 'Неизвестная ошибка'));
        }
    }

     addResultToTable(result) {
        const tbody = document.getElementById('resultsBody');
        if (!tbody) {
            console.error('Results table body not found');
            return;
        }

        const row = document.createElement('tr');
        
        const resultClass = result.result ? 'hit' : 'miss';
        const resultText = result.result ? 'Попадание' : 'Промах';

        // Преобразуем timestamp из ответа (ожидаем миллисекунды). Если нет — используем текущее время.
        const timestampMs = (typeof result.timestamp === 'number' && !isNaN(result.timestamp)) 
            ? Number(result.timestamp) 
            : Date.now();
        const timestampStr = new Date(timestampMs).toLocaleString('ru-RU');

        // executionTime может прийти как число или строка — пытаемся корректно отобразить
        let execTimeVal = result.executionTime;
        if (typeof execTimeVal === 'string') {
            // убрать нецифровые символы и попытаться получить число
            const parsed = parseInt(execTimeVal.replace(/\D/g, ''), 10);
            execTimeVal = isNaN(parsed) ? 0 : parsed;
        } else if (typeof execTimeVal !== 'number' || isNaN(execTimeVal)) {
            execTimeVal = 0;
        }

        let execTimeDisplay = execTimeVal === 0 ? '< 1 мс' : `${execTimeVal} мс`;

        row.innerHTML = `
            <td>${result.x}</td>
            <td>${result.y}</td>
            <td>${result.r}</td>
            <td class="${resultClass}">${resultText}</td>
            <td>${timestampStr}</td>
            <td>${execTimeDisplay.replace(/\s*мс\s*$/, '')} мс</td>
        `;

        // Сохраняем оригинальный timestamp (ms) в data-атрибуте строки — это предотвратит ошибки парсинга локализованной строки.
        row.dataset.timestamp = String(timestampMs);
        row.dataset.executionTime = String(execTimeVal);

        tbody.insertBefore(row, tbody.firstChild);
    }

    drawGraph(r = 3) {
    const canvas = document.getElementById('areaGraph');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');

    const size = 300;
    const center = size / 2;
    const scale = size / 10; // масштаб: 1 единица = size/10 пикселей

    ctx.clearRect(0, 0, size, size);

    // Оси координат
    ctx.strokeStyle = '#000';
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.moveTo(center, 0);
    ctx.lineTo(center, size);
    ctx.moveTo(0, center);
    ctx.lineTo(size, center);
    ctx.stroke();

    // Стрелки
    ctx.fillStyle = '#000';
    ctx.beginPath();
    ctx.moveTo(center, 0);
    ctx.lineTo(center - 5, 10);
    ctx.lineTo(center + 5, 10);
    ctx.fill();
    ctx.beginPath();
    ctx.moveTo(size, center);
    ctx.lineTo(size - 10, center - 5);
    ctx.lineTo(size - 10, center + 5);
    ctx.fill();

    // === Область попадания ===
    ctx.fillStyle = 'rgba(0, 123, 255, 0.4)';
    ctx.strokeStyle = 'rgba(0, 123, 255, 0.8)';

    // --- Четверть круга (I четверть)
ctx.beginPath();
ctx.moveTo(center, center);
ctx.arc(center, center, r * scale, 1.5 * Math.PI, 2 * Math.PI, false);
ctx.closePath();
ctx.fill();
ctx.stroke();


    // --- Прямоугольник (IV четверть)
    ctx.beginPath();
    ctx.rect(center, center, (r / 2) * scale, r * scale);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    // --- Треугольник (II четверть)
    ctx.beginPath();
    ctx.moveTo(center - r * scale, center);        // (-R, 0)
    ctx.lineTo(center, center);                    // (0, 0)
    ctx.lineTo(center, center - (r / 2) * scale);  // (0, R/2)
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    // === Разметка осей ===
    ctx.fillStyle = '#000';
    ctx.font = '12px Arial';
    ctx.textAlign = 'center';

    for (let i = -5; i <= 5; i++) {
        if (i !== 0) {
            const x = center + i * scale;
            const y = center - i * scale;

            ctx.beginPath();
            ctx.moveTo(x, center - 3);
            ctx.lineTo(x, center + 3);
            ctx.stroke();
            ctx.fillText(i, x, center + 15);

            ctx.beginPath();
            ctx.moveTo(center - 3, y);
            ctx.lineTo(center + 3, y);
            ctx.stroke();
            ctx.fillText(i, center - 15, y + 4);
        }
    }
}


    drawPoint(x, y, result) {
        const canvas = document.getElementById('areaGraph');
        if (!canvas) return;

        const ctx = canvas.getContext('2d');
        const center = canvas.width / 2;
        const scale = canvas.width / 10;

        const pointX = center + parseFloat(x) * scale;
        const pointY = center - parseFloat(y) * scale;

        ctx.fillStyle = result ? '#4CAF50' : '#dc3545';
        ctx.beginPath();
        ctx.arc(pointX, pointY, 4, 0, Math.PI * 2);
        ctx.fill();
    }

    saveResults() {
        const tbody = document.getElementById('resultsBody');
        if (!tbody) return;

        const results = [];
        const rows = tbody.querySelectorAll('tr');
        
        rows.forEach(row => {
            const cells = row.querySelectorAll('td');
            if (cells.length >= 6) {
                
                const ts = row.dataset.timestamp ? Number(row.dataset.timestamp) : (new Date(cells[4].textContent).getTime() || 0);
                const exec = row.dataset.executionTime ? Number(row.dataset.executionTime) : parseInt(cells[5].textContent.replace(/\D/g, ''), 10) || 0;

                results.push({
                    x: cells[0].textContent,
                    y: cells[1].textContent,
                    r: cells[2].textContent,
                    result: cells[3].textContent === 'Попадание',
                    timestamp: ts,
                    executionTime: exec
                });
            }
        });
        
        try {
            localStorage.setItem('pointCheckResults', JSON.stringify(results));
        } catch (e) {
            console.error('Error saving to localStorage:', e);
        }
    }

    loadResults() {
        try {
            const saved = localStorage.getItem('pointCheckResults');
            if (saved) {
                const results = JSON.parse(saved);
                results.forEach(result => this.addResultToTable(result));
            }
        } catch (e) {
            console.error('Error loading saved results:', e);
        }
    }

    clearResults() {
        if (confirm('Вы уверены, что хотите очистить все результаты?')) {
            const tbody = document.getElementById('resultsBody');
            if (tbody) {
                tbody.innerHTML = '';
            }
            
            try {
                localStorage.removeItem('pointCheckResults');
            } catch (e) {
                console.error('Error clearing localStorage:', e);
            }
            
            // Перерисовать график без точек
            this.drawGraph();
        }
    }

    showLoading(show) {
        const loadingElement = document.getElementById('loading');
        const submitBtn = document.getElementById('submitBtn');
        
        if (loadingElement) {
            loadingElement.style.display = show ? 'block' : 'none';
        }
        
        if (submitBtn) {
            submitBtn.disabled = show;
        }
    }
}

// Безопасная инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', () => {
    try {
        new PointChecker();
    } catch (error) {
        console.error('Error initializing PointChecker:', error);
    }
});