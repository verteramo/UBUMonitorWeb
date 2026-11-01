// Interfaz ruidosa: Acoplamiento con la red y configuración dinámica de host
@PostExchange
interface MoodleClient {
    fun getCourseParticipants(
        uriBuilderFactory: UriBuilderFactory,         // Inyección dinámica del host
        @RequestHeader("Content-Type") contentType: String = "application/x-www-form-urlencoded",
        @RequestParam("moodlewsrestformat") format: String = "json",
        @RequestParam("wsfunction") function: String       = "core_enrol_get_enrolled_users",
        @RequestParam("wstoken") token: String,       // Dato de seguridad
        @RequestParam("courseid") courseId: Int       // Dato de negocio real
    ): ParticipantsResponse
}

// Uso en el servicio (Acoplamiento severo a la infraestructura)
@Service
class CourseService(
    private val moodleClient: MoodleClient,
    private val request: HttpServletRequest
) {
    fun getParticipants(courseId: Int): ParticipantsResponse {
        // Resolución manual del host desde la solicitud HTTP entrante
        val siteUrl = request.getHeader("X-Site-Url")
            ?: throw IllegalArgumentException("Missing site URL")
        
        val uriFactory = DefaultUriBuilderFactory("$siteUrl/webservice/rest/server.php")
        
        // Obtención manual del token de sesión desde el contexto de seguridad
        val token = SecurityContextHolder.getContext().authentication.credentials.toString()
        
        // Llamada inyectando fábrica, token y dependencias de forma explícita
        return moodleClient.getCourseParticipants(
            uriBuilderFactory = uriFactory,
            token = token,
            courseId = courseId
        )
    }
}