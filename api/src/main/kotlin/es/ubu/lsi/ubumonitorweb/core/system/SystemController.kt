package es.ubu.lsi.ubumonitorweb.core.system

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/system")
class SystemController(
  private val preferencesService: PreferencesService,
) {
  @GetMapping("/preferences")
  fun getPreferences(): Map<String, Any> = preferencesService.read()

  @PutMapping("/preferences")
  fun putPreferences(
    @RequestBody preferences: Map<String, Any>,
  ) = preferencesService.write(preferences)
}
