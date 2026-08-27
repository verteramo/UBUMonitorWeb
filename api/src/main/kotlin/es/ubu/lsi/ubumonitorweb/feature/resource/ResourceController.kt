/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.feature.resource

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/resources")
class ResourceController(
  private val resourceService: ResourceService,
) {
  @GetMapping("/user-icon/{id}/{size:f1|f2|f3}", produces = [MediaType.IMAGE_PNG_VALUE])
  fun getUserIcon(
    @PathVariable id: Int,
    @PathVariable size: String,
  ): ByteArray? = resourceService.getUserIcon(id, size)
}
