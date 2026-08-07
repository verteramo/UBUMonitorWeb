import { effect, Signal, signal, WritableSignal } from '@angular/core';
import { StorageService } from '@core/services/storage.service';

export abstract class AbstractStore<T> {
  protected abstract reduce(oldValue: T, newValue: T): T;

  private signal: WritableSignal<T>;

  constructor(
    key: string,
    storage: StorageService,
    readonly initialValue: T,
  ) {
    this.signal = signal<T>(storage.get<T>(key) ?? initialValue);
    effect(() => storage.set(key, this.signal()));
  }

  get $value(): Signal<T> {
    return this.signal.asReadonly();
  }

  set(newValue: T): void {
    this.signal.update((oldValue) => this.reduce(oldValue, newValue));
  }

  clear() {
    this.signal.set(this.initialValue);
  }
}
