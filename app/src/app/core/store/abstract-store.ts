import { effect, Signal, signal, WritableSignal } from '@angular/core';
import { StorageService } from '@core/services/storage.service';

export abstract class AbstractStore<T> {
  private signal: WritableSignal<T>;

  constructor(
    key: string,
    storage: StorageService,
    readonly initialValue: T,
  ) {
    this.signal = signal<T>(storage.get<T>(key) ?? initialValue);
    effect(() => {
      const value = this.signal();

      if (value == null) {
        storage.remove(key);
      } else {
        storage.set(key, value);
      }
    });
  }

  get $value(): Signal<T> {
    return this.signal.asReadonly();
  }

  get value(): NonNullable<T> {
    return this.signal() as NonNullable<T>;
  }

  set(newValue: T): void {
    this.signal.set(newValue);
  }

  update(updater: (oldValue: T) => T): void {
    this.signal.update(updater);
  }

  clear() {
    this.signal.set(this.initialValue);
  }
}
