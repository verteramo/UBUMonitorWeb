/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

export type LogEntry = {
  datetime: string;
  component: string;
  event: string;
  origin: string;
  ipAddress: string;
  attributes: {
    attemptId?: number;
    chapterId?: number;
    commentId?: number;
    courseId?: number;
    discussionId?: number;
    enrolmentId?: number;
    eventId?: number;
    glossaryEventId: number;
    gradeId?: number;
    gradeItemId?: number;
    groupId?: number;
    h5pId?: number;
    moduleId?: number;
    postId?: number;
    roleId?: number;
    sectionId?: number;
    submissionId?: number;
    tagId?: number;
    targetUserId?: number;
    userId?: number;
  };
};
