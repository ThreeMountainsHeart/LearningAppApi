package com.example.learning_api.quartz.Schedules;

import com.example.learning_api.entity.sql.database.DeadlineEntity;
import com.example.learning_api.quartz.Job.DeadlineReminderJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DeadlineSchedulerService {
    @Autowired
    private Scheduler scheduler;

    public void scheduleTestReminder(DeadlineEntity deadline, long offsetInMillis) throws SchedulerException {
        // Calculate trigger time
        long triggerTimeMillis = Long.parseLong(deadline.getEndDate()) - offsetInMillis;
        Date triggerTime = new Date(triggerTimeMillis);

        // Create JobDetail
        JobDetail jobDetail = JobBuilder.newJob(DeadlineReminderJob.class)
                .withIdentity("DeadlineReminderJob-" + deadline.getId(), "DeadlineReminderGroup")
                .usingJobData("deadlineId", deadline.getId())
                .usingJobData("deadlineTitle", deadline.getTitle())
                .build();

        // Create Trigger
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("Trigger-" + deadline.getId(), "DeadlineReminderGroup")
                .startAt(triggerTime) // Trigger time
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();

        // Schedule job
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
