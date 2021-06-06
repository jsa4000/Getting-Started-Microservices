db.createUser(
  {
    user: "booking",
    pwd: "password",
    roles: [
      {
        role: "readWrite",
        db: "booking"
      }
    ]
  }
);
