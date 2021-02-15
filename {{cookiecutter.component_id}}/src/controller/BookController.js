// const Book = require("./../services/BookService");

function BookController() {
  const listBooks = function(req, res) {
    //Book.list().then(data => res.json(data));
    res.status(200).send("okay");
  };

  const addBooks = function(req, res) {
    //Book.add(req.body).then(data => res.json(data));
    res.status(201).send("okay");
  };

  const deleteBooks = function(req, res) {
    //Book.delete(req.param.id).then(data => res.json(data));
    res.status(200).send("okay");
  };

  return {
    list: listBooks,
    add: addBooks,
    delete: deleteBooks,
  };
}

module.exports = BookController();
