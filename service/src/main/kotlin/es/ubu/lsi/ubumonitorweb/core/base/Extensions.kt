package es.ubu.lsi.ubumonitorweb.core.base

import org.springframework.core.annotation.AnnotatedElementUtils
import java.lang.reflect.Method

/**
 * Función de extensión para la clase [Method] que obtiene una anotación
 * presente en la firma del método o en su clase, en ese orden de prioridad.
 *
 * @param A Tipo de la anotación a buscar.
 * @return Anotación o `null` si no existe.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
inline fun <reified A : Annotation> Method.annotation() =
  AnnotatedElementUtils.getMergedAnnotation(
    this, A::class.java,
  ) ?: AnnotatedElementUtils.getMergedAnnotation(
    declaringClass, A::class.java,
  )

/**
 * Función de extensión para clase [Method] que obtiene la unión de los
 * conjuntos de anotaciones presentes en la firma del método y en la declaración
 * de su clase, en caso de que la anotación no esté presente, devuelve un
 * conjunto vacío.
 *
 * @param A Tipo de la anotación a buscar.
 * @return Conjunto unión con las anotaciones, podría estar vacío.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
inline fun <reified A : Annotation> Method.annotations() =
  AnnotatedElementUtils.getAllMergedAnnotations(
    this, A::class.java,
  ) union AnnotatedElementUtils.getAllMergedAnnotations(
    declaringClass, A::class.java,
  )
