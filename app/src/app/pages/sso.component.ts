import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-sso-callback',
  template: `<p>Procesando autenticación SSO...</p>`,
})
export class SsoCallbackComponent implements OnInit {
  private route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      console.log('Parámetros completos recibidos tras el SSO:', params);
      const token = params['token'];
      if (token) {
        console.log('Token obtenido correctamente:', token);
      } else {
        console.warn('No se encontró el token en la URL de retorno.');
      }
    });
  }
}
