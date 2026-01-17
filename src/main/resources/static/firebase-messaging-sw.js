// Firebase Cloud Messaging Service Worker
importScripts('https://www.gstatic.com/firebasejs/10.7.1/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.7.1/firebase-messaging-compat.js');

// Firebase 설정을 백엔드에서 가져와서 초기화
let messaging = null;

// Service Worker 설치 시 Firebase 설정 가져오기
self.addEventListener('install', (event) => {
    event.waitUntil(
        fetch('http://localhost:8080/api/firebase/config')
            .then(response => response.json())
            .then(config => {
                // Firebase 초기화
                firebase.initializeApp({
                    apiKey: config.apiKey,
                    authDomain: config.authDomain,
                    projectId: config.projectId,
                    storageBucket: config.storageBucket,
                    messagingSenderId: config.messagingSenderId,
                    appId: config.appId,
                    measurementId: config.measurementId
                });

                messaging = firebase.messaging();
                console.log('[SW] Firebase 초기화 완료');
            })
            .catch(error => {
                console.error('[SW] Firebase 설정 로드 실패:', error);
            })
    );
});

// Service Worker 활성화 후 메시지 리스너 등록
self.addEventListener('activate', (event) => {
    console.log('[SW] Service Worker 활성화');
    event.waitUntil(
        // Firebase가 초기화될 때까지 대기
        new Promise((resolve) => {
            const checkMessaging = setInterval(() => {
                if (messaging) {
                    // 백그라운드 메시지 수신 처리
                    messaging.onBackgroundMessage((payload) => {
                        console.log('[SW] 백그라운드 메시지 수신:', payload);

                        const notificationTitle = payload.notification?.title || '새 알림';
                        const notificationOptions = {
                            body: payload.notification?.body || '내용이 없습니다.',
                            icon: payload.notification?.icon || '/favicon.ico',
                            badge: '/favicon.ico',
                            data: payload.data
                        };

                        self.registration.showNotification(notificationTitle, notificationOptions);
                    });

                    clearInterval(checkMessaging);
                    resolve();
                }
            }, 100);
        })
    );
});

// 알림 클릭 이벤트 처리
self.addEventListener('notificationclick', (event) => {
    console.log('[SW] 알림 클릭:', event);

    event.notification.close();

    // 알림에 URL이 포함되어 있으면 해당 페이지로 이동
    if (event.notification.data && event.notification.data.url) {
        event.waitUntil(
            clients.openWindow(event.notification.data.url)
        );
    }
});
