import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const router = inject(Router);
    const userJson = localStorage.getItem('user');
    let token = '';

    if (userJson) {
        try {
            const user = JSON.parse(userJson);
            token = user.token || '';
        } catch (e) {
            console.error('Error parsing user from localStorage', e);
        }
    }

    let authReq = req;
    if (token) {
        console.log('AuthInterceptor: TOKEN FOUND, attaching to header. Token length:', token.length);
        authReq = req.clone({
            headers: req.headers.set('Authorization', `Bearer ${token}`)
        });
    } else {
        console.warn('AuthInterceptor: NO TOKEN found in localStorage for user.');
    }

    return next(authReq).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401) {
                console.warn('AuthInterceptor: 401 Unauthorized detected. Redirecting to login.');
                localStorage.removeItem('user');
                router.navigate(['/']);
                // Optional: Show a toast or alert here if possible, but router nav is priority
            }
            return throwError(() => error);
        })
    );
};
