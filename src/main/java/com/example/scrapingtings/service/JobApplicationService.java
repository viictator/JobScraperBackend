package com.example.scrapingtings.service;

import com.example.scrapingtings.model.JobApplication;
import com.example.scrapingtings.model.ScrapingJob;
import com.example.scrapingtings.model.User;
import com.example.scrapingtings.repository.JobApplicationRepository;
import com.example.scrapingtings.repository.JobRepository;
import com.example.scrapingtings.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JobApplicationService {

    @Autowired
    JobApplicationRepository jobApplicationRepository;

    private final ChatClient.Builder chatClientBuilder;
    private ChatClient chatClient;


    private String myProfileText;
    @Value("classpath:myprofile.txt")
    private Resource myProfileResource;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private UserRepository userRepository;

    public JobApplicationService(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    @PostConstruct
    public void init() throws IOException {
        this.chatClient = chatClientBuilder.build();
        this.myProfileText = myProfileResource.getContentAsString(StandardCharsets.UTF_8);
    }


    public List<JobApplication> generateAllApplications(String username) {
        List<ScrapingJob> allJobs = jobRepository.findAll();
        List<JobApplication> allApplications = new ArrayList<>();

        allJobs.forEach((job) -> {
            allApplications.add(generateApplication(job.getId(), username));
        });

        System.out.println();
        return allApplications;
    }


    public JobApplication generateApplication(int jobId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with username " + username + " not found."
                ));
        int userId = user.getId();
        String personalName = user.getPersonalName();
        String personalEmail = user.getPersonalEmail();
        String personalAddress = user.getPersonalAddress();
        String profileText = user.getProfileText();

        ScrapingJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job with ID " + jobId + " not found in the database."
                ));
        String companyName = job.getCompanyName();
        String jobTitle = job.getJobTitle();

        String jobDescription = job.getDescription() == null ? "" : job.getDescription();



        String promptText = """
            You are a highly skilled professional career assistant. Your goal is to write a single,
            persuasive, and highly customized job application letter for the applicant based on 
            the provided information.
            
            **INSTRUCTIONS:**
            1. The letter must be addressed formally to "The Hiring Team at {companyName}".
            2. It must be concise: **3 to 4 paragraphs maximum**.
            3. In the body, explicitly draw **direct connections** between the applicant's 
               **profile** (skills and experience) and the **job description** (requirements).
            4. End with a professional closing and the applicant's name.
            5. The job might be in Danish or English, write the application accordingly          
            6. Make sure to include the {personalName}, {personalEmail}, {personalAddress} at the top of the application, and the generated application below it. Each with a line break 'ENTER' between them.
            
            ---
            
            **APPLICANT PROFILE:**
            {personalName}
            {personalEmail}
            {personalAddress}
            {profileText}
            
            ---
            
            **JOB DETAILS:**
            - Title: {jobTitle}
            - Description: {jobDescription}
            
            ---
            
            **GENERATED JOB APPLICATION (Start Here):**
            """;

        PromptTemplate promptTemplate = new PromptTemplate(promptText);

        Map<String, Object> model = Map.of(
                "personalName", personalName,
                "personalEmail", personalEmail,
                "personalAddress", personalAddress,
                "profileText", profileText,
                "jobTitle", jobTitle,
                "companyName", companyName,
                "jobDescription", jobDescription
        );

        String content = chatClient.prompt(promptTemplate.create(model)).call().content();

        JobApplication newJobApp = new JobApplication(jobId, userId, jobTitle, companyName, content);
        jobApplicationRepository.save(newJobApp);
        return newJobApp;

    }



}
