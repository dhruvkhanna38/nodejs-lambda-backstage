const { TestScheduler } = require("jest");
const BookController = require("../../src/controller/BookController");


describe("Book Controller Function", ()=>{
    test("Should contain list property", ()=>{
        expect(BookController).toHaveProperty('list');
    })

    test("Should contain add property", ()=>{
        expect(BookController).toHaveProperty('add');
    })

    test("Should Contain delete property", ()=>{
        expect(BookController).toHaveProperty('delete');
    })

    describe("listBooks function", ()=>{
        test("should have a definition", ()=>{
            expect(BookController.list).toBeDefined();
        })
    })

    describe("addBooks function", ()=>{
        test("should have a definition", ()=>{
            expect(BookController.add).toBeDefined();
        })
    })

    describe("deleteBooks function", ()=>{
        test("should have a definition", ()=>{
            expect(BookController.delete).toBeDefined();
        })
    })
})
