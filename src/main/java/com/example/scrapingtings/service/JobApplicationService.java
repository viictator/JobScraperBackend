package com.example.scrapingtings.service;

import com.example.scrapingtings.model.JobApplication;
import com.example.scrapingtings.repository.JobApplicationRepository;
import com.example.scrapingtings.repository.JobRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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

    public JobApplicationService(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    @PostConstruct
    public void init() throws IOException {
        this.chatClient = chatClientBuilder.build();
        this.myProfileText = myProfileResource.getContentAsString(StandardCharsets.UTF_8);
    }


    public JobApplication generateApplication(String jobTitle, String companyName, String jobDescription) {
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
            
            ---
            
            **APPLICANT PROFILE:**
            {myProfile}
            
            ---
            
            **JOB DETAILS:**
            - Title: {jobTitle}
            - Description: {jobDescription}
            
            ---
            
            **GENERATED JOB APPLICATION (Start Here):**
            """;

        PromptTemplate promptTemplate = new PromptTemplate(promptText);

        Map<String, Object> model = Map.of(
                "myProfile", this.myProfileText,
                "jobTitle", jobTitle,
                "companyName", companyName,
                "jobDescription", jobDescription
        );

        String content = chatClient.prompt(promptTemplate.create(model)).call().content();

        JobApplication newJobApp = new JobApplication(jobTitle, companyName, content);
        jobApplicationRepository.save(newJobApp);
        return newJobApp;

    }

}
