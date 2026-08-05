import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

export interface TokenResponse { accessToken: string; expiresIn: number; }
export interface OperationStatus { status: string; userId: string; clientId: string; observedAt: string; }
export interface AuditEvent {
  occurredAt: string; outcome: string; reason: string; userId: string; clientId: string;
  method: string; path: string; correlationId: string;
}

@Injectable({ providedIn: 'root' })
export class GatewayApiService {
  constructor(private readonly http: HttpClient) {}

  issueToken(profile: 'operator' | 'administrator') {
    return this.http.post<TokenResponse>('/api/dev/tokens', { profile });
  }

  operationStatus(token: string, apiKey: string) {
    return this.http.get<OperationStatus>('/api/operations/status', { headers: this.headers(token, apiKey) });
  }

  audit(token: string, apiKey: string) {
    return this.http.get<AuditEvent[]>('/api/administration/audit', { headers: this.headers(token, apiKey) });
  }

  private headers(token: string, apiKey: string): HttpHeaders {
    return new HttpHeaders({ Authorization: `Bearer ${token}`, 'X-Api-Key': apiKey });
  }
}
