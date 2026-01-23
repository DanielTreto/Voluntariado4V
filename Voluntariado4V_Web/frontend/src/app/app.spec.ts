import { TestBed } from '@angular/core/testing';
import { App } from './app';
import { ApiService } from './services/api.service';
import { of } from 'rxjs';
import { provideRouter } from '@angular/router';

describe('App', () => {
  let apiServiceSpy: jasmine.SpyObj<ApiService>;

  beforeEach(async () => {
    apiServiceSpy = jasmine.createSpyObj('ApiService', ['getUsuarios']);
    apiServiceSpy.getUsuarios.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        { provide: ApiService, useValue: apiServiceSpy },
        provideRouter([])
      ]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
