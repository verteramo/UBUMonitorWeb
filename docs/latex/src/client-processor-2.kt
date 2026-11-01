// =======================================================================
// Providers (Beans): Lógica de infraestructura aislada y reutilizable
// =======================================================================

@Component
class SiteUrlProvider(
    private val request: HttpServletRequest
) : PropertyProvider.Static<String?> {
    override fun resolve(context: MethodContext): String? {
        return request.getHeader("X-Site-Url")
    }
}

@Component
class TokenProvider : PropertyProvider.Static<String?> {
    override fun resolve(context: MethodContext): String? {
        return SecurityContextHolder.getContext().authentication.credentials
    }
}

// =======================================================================
// Cliente y servicio
// =======================================================================

// Interfaz limpia: Centrada exclusivamente en el negocio
@Client("webservice-client")
interface MoodleClient {
    fun getCourseParticipants(
        @RequestParam("courseid") courseId: Int // Único dato de dominio
    ): ParticipantsResponse
}

// Uso en el servicio (Inyección completamente transparente)
@Service
class CourseService(private val moodleClient: MoodleClient) {
    fun getParticipants(courseId: Int): ParticipantsResponse {
        // El ClientProcessor construye todo el contexto por debajo
        return moodleClient.getCourseParticipants(courseId = courseId)
    }
}
