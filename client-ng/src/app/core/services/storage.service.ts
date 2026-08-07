import { inject, InjectionToken } from '@angular/core';

interface Codec<Source, Target> {
  encode: (value: Source) => Target;
  decode: (value: Target) => Source;
}

type Serializer = Codec<any, string>;

type Transformer = Codec<string, string>;

export class StorageService {
  private serializer = inject(STORAGE_SERIALIZER);
  private transformers = inject(STORAGE_TRANSFORMERS);

  constructor(private storage: Storage) {}

  get<T>(key: string): T | null {
    const item = this.storage.getItem(key);

    if (item) {
      try {
        const data = this.transformers.reduceRight(
          (current, transformer) => transformer.decode(current),
          item,
        );

        return this.serializer.decode(data);
      } catch {
        return null;
      }
    }

    return null;
  }

  set<T>(key: string, value: T) {
    const data = this.serializer.encode(value);
    const item = this.transformers.reduce((data, transformer) => transformer.encode(data), data);

    this.storage.setItem(key, item);
  }

  remove(key: string) {
    this.storage.removeItem(key);
  }

  clear() {
    this.storage.clear();
  }
}

export const STORAGE_SERIALIZER = new InjectionToken<Serializer>('storageSerializer', {
  providedIn: 'root',
  factory: () => ({
    encode: (value) => JSON.stringify(value),
    decode: (value) => JSON.parse(value),
  }),
});

export const STORAGE_TRANSFORMERS = new InjectionToken<Transformer[]>('storageTransformers', {
  providedIn: 'root',
  factory: () => [
    {
      encode: (value) => encodeURIComponent(value),
      decode: (value) => decodeURIComponent(value),
    },
    {
      encode: (value) => btoa(value),
      decode: (value) => atob(value),
    },
  ],
});

export const LOCAL_STORAGE = new InjectionToken<StorageService>('localStorage', {
  providedIn: 'root',
  factory: () => new StorageService(localStorage),
});

export const SESSION_STORAGE = new InjectionToken<StorageService>('sessionStorage', {
  providedIn: 'root',
  factory: () => new StorageService(sessionStorage),
});
