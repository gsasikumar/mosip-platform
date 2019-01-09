import { Injectable } from '@angular/core';
import { UserModel } from './demographic/modal/user.modal';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private users: UserModel[] = [];
  usersChanged = new Subject<UserModel[]>();
  private regCenterId: string;

  flushUsers() {
    this.users.length = 0;
  }

  getUser(index: number) {
    return this.users[index];
  }

  getUsers() {
    return this.users.slice();
  }

  addUser(user: UserModel) {
    this.users.push(user);
    console.log('users after being pushed', this.users);

    this.usersChanged.next(this.users.slice());
  }

  addUsers(users: UserModel[]) {
    this.users = users;
    this.usersChanged.next(this.users.slice());
  }

  updateUser(index: number, newUser: UserModel) {
    this.users[index] = newUser;
    this.usersChanged.next(this.users.slice());
  }

  deleteUser(index: number) {
    this.users.splice(index, 1);
    this.usersChanged.next(this.users.slice());
  }

  getUserFiles(index: number) {
    return this.users[index].files.slice();
  }

  setRegCenterId(id: string) {
    this.regCenterId = id;
  }

  getRegCenterId() {
    return this.regCenterId;
  }
}
