  ### Summary of Unit Tests                                                       
                                                                            
  1. AIControllerTest                                                       
  (src/test/java/com/example/llm/controller/AIControllerTest.java)          
                                                                            
  Tests for all four endpoints using @WebMvcTest with MockMvc:              
                                                                            
  /api/ai/classify (3 tests)                                                
  - Valid text classification response                                      
  - Empty labels handling                                                   
  - Service exception propagation                                           
                                                                            
  /api/ai/sentiment (4 tests)                                               
  - Positive sentiment analysis                                             
  - Negative sentiment analysis                                             
  - Neutral sentiment analysis                                              
  - Service exception propagation                                           
                                                                            
  /api/ai/summarize (3 tests)                                               
  - Valid summary with key points                                           
  - Short text handling                                                     
  - Service exception propagation                                           
                                                                            
  /api/ai/intent (5 tests)                                                  
  - Question intent detection                                               
  - Command intent detection                                                
  - Request intent detection                                                
  - Statement intent detection                                              
  - Service exception propagation                                           
                                                                            
  Request validation (6 tests)                                              
  - Null text field handling                                                
  - Empty text handling                                                     
  - Very long text handling                                                 
  - Special characters handling                                             
  - Invalid JSON (400 Bad Request)                                          
  - Non-JSON content type (415 Unsupported Media Type)                      
                                                                            
  2. AIServiceTest                                                          
  (src/test/java/com/example/llm/service/AIServiceTest.java)                
                                                                            
  Tests for the service layer with mocked ChatClient:                       
                                                                            
  classifyText (5 tests)                                                    
  - Valid JSON parsing                                                      
  - Markdown code block stripping (\json`)                                  
  - Plain code block stripping                                              
  - Invalid JSON exception handling                                         
  - Empty labels array                                                      
                                                                            
  analyzeSentiment (4 tests)                                                
  - Positive sentiment parsing                                              
  - Negative sentiment parsing                                              
  - Neutral sentiment with empty emotions                                   
  - Markdown wrapped response                                               
                                                                            
  summarizeText (4 tests)                                                   
  - Valid summary response                                                  
  - Single key point                                                        
  - Empty key points                                                        
  - Malformed JSON exception                                                
                                                                            
  detectIntent (5 tests)                                                    
  - Question intent                                                         
  - Command intent                                                          
  - Request intent                                                          
  - Statement intent                                                        
  - Markdown code block wrapper                                             
                                                                            
  JSON parsing edge cases (4 tests)                                         
  - Extra whitespace handling                                               
  - Unknown fields exception                                                
  - Trailing code block handling                                            
  - ChatClient prompt structure verification   