import React from 'react';

const CreateUser = ({ newUser, setNewUser, createUser }) => (
  <div className="create-user">
    <h2>Create User</h2>
    <input
      type="text"
      placeholder="Username"
      value={newUser.username}
      onChange={(e) => setNewUser({ ...newUser, username: e.target.value })}
    />
    <input
      type="text"
      placeholder="Name"
      value={newUser.name}
      onChange={(e) => setNewUser({ ...newUser, name: e.target.value })}
    />
    <input
      type="email"
      placeholder="Email"
      value={newUser.email}
      onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
    />
    <input
      type="password"
      placeholder="Password"
      value={newUser.password}
      onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
    />
    <input
      type="text"
      placeholder="Address"
      value={newUser.address}
      onChange={(e) => setNewUser({ ...newUser, address: e.target.value })}
    />
    <input
      type="text"
      placeholder="Bio"
      value={newUser.bio}
      onChange={(e) => setNewUser({ ...newUser, bio: e.target.value })}
    />
    <input
      type="text"
      placeholder="Image URL"
      value={newUser.img_url}
      onChange={(e) => setNewUser({ ...newUser, img_url: e.target.value })}
    />
    <button onClick={createUser}>Create User</button>
  </div>
);

export default CreateUser;
