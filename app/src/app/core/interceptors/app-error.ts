/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { HttpErrorResponse } from '@angular/common/http';

/**
 * Mensaje de error de fallback para casos donde no se puede determinar el error ocurrido.
 */
const MESSAGE_FALLBACK = $localize`Unknown error`;

/**
 * Mapa de mensajes de error normalizados y localizados.
 */
const MESSAGES: Record<number, string> = {
  401: $localize`Invalid login`,
  403: $localize`Session expired`,
  502: $localize`Server unreachable`,
};

/**
 * Tipo de error que se maneja dentro de la aplicación.
 * Se construye a partir de un `HttpErrorResponse` y hereda
 * de `Error` para asegurar la compatibilidad con `rxResource`.
 *
 * Si el servidor no responde, `HttpErrorResponse` se instancia con
 * `status === 0`, por lo que a efectos internos es como un `502 Bad Gateway`.
 *
 * La propiedad error es de tipo `any`, pero como se tiene control del
 * backend, este devuelve un objeto que cumple con la interfaz `ProblemDetail`:
 * https://datatracker.ietf.org/doc/html/rfc7807/#section-3.1, esto implica
 * que en la propiedad `response.error.detail` se tiene un mensaje de error.
 */
export class AppError extends Error {
  /**
   * Código de estado.
   */
  public readonly status: number;

  /**
   * URL en la que ha ocurrido el error.
   */
  public readonly url: string | null;

  /**
   * Constructor.
   *
   * @param response Error HTTP ocurrido.
   */
  constructor(response: HttpErrorResponse) {
    super(MESSAGES[response.status || 502] || response.error?.detail || MESSAGE_FALLBACK);
    this.status = response.status;
    this.url = response.url;
  }
}
