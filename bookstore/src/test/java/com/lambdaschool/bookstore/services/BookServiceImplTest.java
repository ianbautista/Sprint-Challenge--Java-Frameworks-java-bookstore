package com.lambdaschool.bookstore.services;

import com.lambdaschool.bookstore.BookstoreApplication;
import com.lambdaschool.bookstore.exceptions.ResourceNotFoundException;
import com.lambdaschool.bookstore.models.Author;
import com.lambdaschool.bookstore.models.Book;
import com.lambdaschool.bookstore.models.Section;
import com.lambdaschool.bookstore.models.Wrote;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BookstoreApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//**********
// Note security is handled at the controller, hence we do not need to worry about security here!
//**********
public class BookServiceImplTest
{

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private SectionService sectionService;

    @Before
    public void setUp() throws
            Exception
    {
        MockitoAnnotations.initMocks(this);

        // getting book id
        List<Book> myList = bookService.findAll();
        for (Book b : myList)
        {
            System.out.println(b.getBookid() + " " + b.getTitle());
        }

//        26 Flatterland
//        27 Digital Fortess
//        28 The Da Vinci Code
//        29 Essentials of Finance
//        30 Calling Texas Home

    }

    @After
    public void tearDown() throws
            Exception
    {
    }

    @Test
    public void a_findAll()
    {
        assertEquals(5, bookService.findAll().size());
    }

    @Test
    public void findBookById()
    {
        assertEquals("The Da Vinci Code",bookService.findBookById(28).getTitle());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void notFindBookById()
    {
        assertEquals("The Turtle Book",bookService.findBookById(1000).getTitle());
    }

    @Test
    public void z_delete()
    {
        bookService.delete(30);
        assertEquals(4, bookService.findAll().size());
    }

    @Test
    public void save()
    {
        // author
        Author a7 = new Author("Christian", "Bautista");
        a7 = authorService.save(a7);

        // section
        Section s6 = new Section("Turtle");
        s6 = sectionService.save(s6);

        // new book
        Book b1 = new Book("Java Spring through the Wisdom of the Turtle", "9780738206752", 2020, s6);
        b1.getWrotes().add(new Wrote(a7, new Book()));
        Book newBook = bookService.save(b1);

        assertNotNull(newBook);
        assertEquals("Java Spring through the Wisdom of the Turtle",newBook.getTitle());

    }

    @Test
    public void update()
    {
    }

    @Test
    public void deleteAll()
    {
    }
}