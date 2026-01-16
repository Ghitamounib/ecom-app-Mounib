package org.example.chatbotservice.services;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:docs/cours.pdf") // Default document
    private Resource pdfResource;

    public RagService(ChatClient chatClient, EmbeddingClient embeddingClient) {
        this.chatClient = chatClient;
        this.vectorStore = new SimpleVectorStore(embeddingClient); // In-memory for simplicity
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        // Ingest the default document on startup asynchronously
        new Thread(() -> {
            System.out.println("Starting async document ingestion...");
            ingestDocument(null);
            System.out.println("Document ingestion completed.");
        }).start();
    }

    public void ingestDocument(Resource resource) {
        if (resource == null)
            resource = pdfResource;

        try {
            PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource);
            List<Document> documents = pdfReader.get();

            // Split into chunks
            TokenTextSplitter splitter = new TokenTextSplitter();
            List<Document> chunks = splitter.apply(documents);

            // Add to Vector Store
            vectorStore.add(chunks);
            System.out.println("Ingested " + chunks.size() + " chunks.");
        } catch (Exception e) {
            System.err.println("Error ingesting document: " + e.getMessage());
        }
    }

    public String ask(String query) {
        // 1. Retrieve similar documents
        List<Document> similarDocuments = vectorStore.similaritySearch(query);

        String context = similarDocuments.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n"));

        // 2. Construct Prompt
        String systemPrompt = "You are a helpful assistant. Use the following context to answer the question. If you don't know, say so.\nContext:\n"
                + context;
        String userPrompt = "Question: " + query;

        // 3. Call OpenAI
        return chatClient.call(systemPrompt + "\n" + userPrompt);
    }
}
