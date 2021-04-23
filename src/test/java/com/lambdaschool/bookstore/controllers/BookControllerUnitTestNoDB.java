package com.lambdaschool.bookstore.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaschool.bookstore.BookstoreApplicationTest;
import com.lambdaschool.bookstore.models.Author;
import com.lambdaschool.bookstore.models.Book;
import com.lambdaschool.bookstore.models.Section;
import com.lambdaschool.bookstore.models.Wrote;
import com.lambdaschool.bookstore.services.BookService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BookstoreApplicationTest.class)
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
@AutoConfigureMockMvc
public class BookControllerUnitTestNoDB
{
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    List<Book> myBookList = new ArrayList<>();

    @Before
    public void setUp() throws
            Exception
    {

        Author a1 = new Author("John", "Mitchell");
        a1.setAuthorid(1);
        Author a2 = new Author("Dan", "Brown");
        a1.setAuthorid(2);
        Author a3 = new Author("Jerry", "Poe");
        a1.setAuthorid(3);
        Author a4 = new Author("Wells", "Teague");
        a1.setAuthorid(4);
        Author a5 = new Author("George", "Gallinger");
        a1.setAuthorid(5);
        Author a6 = new Author("Ian", "Stewart");
        a1.setAuthorid(6);

        Section s1 = new Section("Fiction");
        s1.setSectionid(1);
        Section s2 = new Section("Technology");
        s2.setSectionid(2);
        Section s3 = new Section("Travel");
        s3.setSectionid(3);
        Section s4 = new Section("Business");
        s4.setSectionid(4);
        Section s5 = new Section("Religion");
        s5.setSectionid(5);

        Book b1 = new Book("test Flatterland", "9780738206752", 2001, s1);
        b1.setBookid(1);
        b1.getWrotes()
            .add(new Wrote(a6, b1));
        myBookList.add(b1);

        Book b2 = new Book("test Digital Fortess", "9788489367012", 2007, s1);
        b2.setBookid(2);
        b2.getWrotes()
            .add(new Wrote(a2, b2));
        myBookList.add(b2);

        Book b3 = new Book("test The Da Vinci Code", "9780307474278", 2009, s1);
        b3.setBookid(3);
        b3.getWrotes()
            .add(new Wrote(a2, b3));
        myBookList.add(b3);

        Book b4 = new Book("test Essentials of Finance", "1314241651234", 0, s4);
        b4.setBookid(4);
        b4.getWrotes()
            .add(new Wrote(a3, b4));
        b4.getWrotes()
            .add(new Wrote(a5, b4));
        myBookList.add(b4);

        Book b5 = new Book("test Calling Texas Home", "1885171382134", 2000, s3);
        b5.setBookid(5);
        b5.getWrotes()
            .add(new Wrote(a4, b5));
        myBookList.add(b5);

        System.out.println("Size " + myBookList.size());
        for (Book b : myBookList)
        {
            System.out.println(b);
        }

        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @After
    public void tearDown() throws
            Exception
    {
    }

    @Test
    public void listAllBooks() throws
            Exception
    {
        Mockito.when(bookService.findAll())
                .thenReturn(myBookList);

        RequestBuilder requestBody = MockMvcRequestBuilders.get("/books/books")
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBody)
                .andReturn();

        String testResult = result.getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();

        String expectedResult = mapper.writeValueAsString(myBookList);

        assertEquals(expectedResult, testResult);
    }

    @Test
    public void getBookById() throws
            Exception
    {
        Mockito.when(bookService.findBookById((long) 5))
                .thenReturn(myBookList.get(4));

        RequestBuilder requestBody = MockMvcRequestBuilders.get("/books/book/5")
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBody)
                .andReturn();

        String testResult = result.getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();

        String expectedResult = mapper.writeValueAsString(myBookList.get(4));

        assertEquals(expectedResult, testResult);

    }

    @Test
    public void getNoBookById() throws
            Exception
    {
        Mockito.when(bookService.findBookById((long) 69))
                .thenReturn(null);

        RequestBuilder requestBody = MockMvcRequestBuilders.get("/books/book/69")
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBody)
                .andReturn();

        String testResult = result.getResponse()
                .getContentAsString();

        String expectedResult = "";

        assertEquals(expectedResult, testResult);
    }

    @Test
    public void addNewBook() throws
            Exception
    {
        Section newSection = new Section("Not Real");
        newSection.setSectionid(69);


        Book newBook = new Book("test book","6969696969696", 6900, newSection);

        newBook.getWrotes().add(new Wrote((new Author("Some", "Guy")), newBook ));
        newBook.setBookid(69);


        ObjectMapper mapper = new ObjectMapper();
        String BookString = mapper.writeValueAsString(newBook);

        Mockito.when(bookService.save(any(Book.class)))
                .thenReturn(newBook);

        RequestBuilder rb = MockMvcRequestBuilders.post("/books/book")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(BookString);

        mockMvc.perform(rb)
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateFullBook()
    {
    }

    @Test
    public void deleteBookById() throws
            Exception
    {

        RequestBuilder rb = MockMvcRequestBuilders.delete("/books/book/{bookid}", "4")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(rb)
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}