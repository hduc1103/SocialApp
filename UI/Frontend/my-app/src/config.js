export const PUBLIC_URL = 'http://localhost:3001';
export const BASE_URL = 'http://localhost:8080';
export function showGreenNotification(message) {
    const notification = document.createElement('div');
    notification.textContent = message;
    notification.style.position = 'fixed';
    notification.style.top = '20px';
    notification.style.right = '20px';
    notification.style.backgroundColor = '#4CAF50'; 
    notification.style.color = 'white';
    notification.style.padding = '15px';
    notification.style.borderRadius = '5px';
    notification.style.zIndex = '1000';
    notification.style.opacity = '0';
    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.opacity = '1';
    }, 10); 

    setTimeout(() => {
        notification.style.opacity = '0'; 
        setTimeout(() => {
            document.body.removeChild(notification); 
        }, 300); 
    }, 3000);
}

export function showRedNotification(message) {
    const notification = document.createElement('div');
    notification.textContent = message;
    notification.style.position = 'fixed';
    notification.style.top = '20px';
    notification.style.right = '20px';
    notification.style.backgroundColor = '#d60000'; 
    notification.style.color = 'white';
    notification.style.padding = '15px';
    notification.style.borderRadius = '5px';
    notification.style.zIndex = '1000';
    notification.style.opacity = '0';
    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.opacity = '1';
    }, 10); 

    setTimeout(() => {
        notification.style.opacity = '0'; 
        setTimeout(() => {
            document.body.removeChild(notification); 
        }, 300); 
    }, 3000);
}
