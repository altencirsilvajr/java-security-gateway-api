import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { AuditEvent, GatewayApiService, OperationStatus } from './gateway-api.service';

@Component({
  selector: 'app-root',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  readonly token = signal('');
  readonly operation = signal<OperationStatus | null>(null);
  readonly audits = signal<AuditEvent[]>([]);
  readonly timeline = signal<string[]>([]);
  readonly busy = signal(false);
  readonly credentials = new FormGroup({
    profile: new FormControl<'operator' | 'administrator'>('operator', { nonNullable: true }),
    apiKey: new FormControl('', { nonNullable: true, validators: [Validators.required] })
  });

  constructor(private readonly api: GatewayApiService) {}

  async issueToken(): Promise<void> {
    await this.run('Emitir JWT efêmero', async () => {
      const response = await firstValueFrom(this.api.issueToken(this.credentials.controls.profile.value));
      this.token.set(response.accessToken);
      this.operation.set(null);
      this.audits.set([]);
    });
  }

  async callOperation(): Promise<void> {
    await this.withCredentials('Consultar operação', async (token, key) => {
      this.operation.set(await firstValueFrom(this.api.operationStatus(token, key)));
    });
  }

  async loadAudit(): Promise<void> {
    await this.withCredentials('Consultar auditoria', async (token, key) => {
      this.audits.set(await firstValueFrom(this.api.audit(token, key)));
    });
  }

  forgetCredentials(): void {
    this.token.set('');
    this.credentials.controls.apiKey.setValue('');
    this.operation.set(null);
    this.audits.set([]);
    this.note('Credenciais removidas da memória');
  }

  private async withCredentials(label: string, action: (token: string, key: string) => Promise<void>): Promise<void> {
    if (!this.token() || this.credentials.controls.apiKey.invalid) {
      this.note(`${label} → emita o token e informe a API key`);
      return;
    }
    await this.run(label, () => action(this.token(), this.credentials.controls.apiKey.value));
  }

  private async run(label: string, action: () => Promise<void>): Promise<void> {
    this.busy.set(true);
    try {
      await action();
      this.note(`${label} → sucesso`);
    } catch (failure: any) {
      this.note(`${label} → ${failure?.status ?? 'erro'} ${failure?.error?.code ?? failure?.message ?? ''}`);
    } finally {
      this.busy.set(false);
    }
  }

  private note(message: string): void {
    this.timeline.update(items => [`${new Date().toLocaleTimeString()}  ${message}`, ...items].slice(0, 12));
  }
}
