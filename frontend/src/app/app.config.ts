import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { initializeApp, provideFirebaseApp } from '@angular/fire/app';
import { getAuth, provideAuth } from '@angular/fire/auth';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideHttpClient(),
    provideFirebaseApp(() => initializeApp({
        apiKey: "AIzaSyCM7P5pA-2fcSDbuQKO3kcxLwe3VFhTbTw",
        authDomain: "voluntariado4v-29430.firebaseapp.com",
        projectId: "voluntariado4v-29430",
        storageBucket: "voluntariado4v-29430.firebasestorage.app",
        messagingSenderId: "645116961397",
        appId: "1:645116961397:web:0555b75dc625aea647ce3c",
        measurementId: "G-Y87KHEGMQY"
    })),
    provideAuth(() => getAuth())
  ]
};
