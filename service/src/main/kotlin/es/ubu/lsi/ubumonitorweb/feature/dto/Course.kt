package es.ubu.lsi.ubumonitorweb.feature.dto

interface Course {
  val id: Int
  val fullname: String
  val summary: String?
  val summaryformat: Int?
  val courseimage: String?
  val isfavourite: Boolean?
  val startdate: Long?
  val enddate: Long?

  fun normalize(category: MoodleCategory) =
    NormalizedCourse(
      id = id,
      fullname = fullname,
      summary = summary,
      summaryformat = summaryformat,
      courseimage = courseimage,
      isfavourite = isfavourite,
      startdate = startdate,
      enddate = enddate,
      category = category,
    )
}
