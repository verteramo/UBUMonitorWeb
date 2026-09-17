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
    categoryId?: number;
    chapterId?: number;
    choiceId?: number;
    commentId?: number;
    courseId?: number;
    discussionId?: number;
    elementName?: string;
    enrolmentId?: number;
    enrolmentMethod?: string;
    eventId?: number;
    eventName?: string;
    evidenceId?: number;
    fieldId?: number;
    glossaryEntryId?: number;
    gradeId?: number;
    gradeItemId?: number;
    groupId?: number;
    groupingId?: number;
    h5pId?: number;
    isModuleCompleted?: boolean;
    itemId?: number;
    itemType?: string;
    markerId?: number;
    moduleId?: number;
    moduleName?: string;
    moduleType?: string;
    noteId?: number;
    optionId?: number;
    overrideId?: number;
    pageId?: number;
    pageType?: string;
    postId?: number;
    questionCategoryId?: number;
    questionId?: number;
    questionType?: string;
    recordId?: number;
    reportName?: string;
    roleId?: number;
    ruleId?: number;
    scormId?: number;
    scormValue?: string;
    searchTerm?: string;
    sectionId?: number;
    sectionName?: string;
    stepId?: number;
    stepIndex?: number;
    submissionFilesCount?: number;
    submissionId?: number;
    submissionStatus?: string;
    submissionWordsCount?: number;
    subscriptionId?: number;
    tagId?: number;
    tourId?: number;
    url?: string;
    userCompetencyId?: number;
    userCompetencyRating?: number;
    userId?: number;
    viewingMode?: string;
    workflowState?: string;
    workshopPhase?: number;
  };
};
