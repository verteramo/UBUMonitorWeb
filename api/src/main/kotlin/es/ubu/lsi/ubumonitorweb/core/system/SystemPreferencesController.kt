package es.ubu.lsi.ubumonitorweb.core.system

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/system/preferences")
class SystemPreferencesController(
  private val service: SystemPreferencesService,
) {
  @GetMapping
  fun get(): SystemPreferences = service.read()

  @PutMapping
  fun put(
    @RequestBody preferences: SystemPreferences,
  ) = service.write(preferences)
}
