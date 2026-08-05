import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { GatewayApiService } from './gateway-api.service';

describe('GatewayApiService', () => {
  it('sends both identities without putting credentials in the URL', () => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const service = TestBed.inject(GatewayApiService);
    const http = TestBed.inject(HttpTestingController);

    service.operationStatus('signed.jwt', 'secret-api-key').subscribe();

    const request = http.expectOne('/api/operations/status');
    expect(request.request.headers.get('Authorization')).toBe('Bearer signed.jwt');
    expect(request.request.headers.get('X-Api-Key')).toBe('secret-api-key');
    expect(request.request.url).not.toContain('secret-api-key');
    request.flush({ status: 'available', userId: 'operator', clientId: 'client', observedAt: new Date().toISOString() });
    http.verify();
  });
});
