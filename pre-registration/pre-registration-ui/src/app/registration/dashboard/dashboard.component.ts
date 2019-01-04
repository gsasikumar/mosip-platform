import { Component, OnInit, ChangeDetectorRef } from '@angular/core';

import { Router, ActivatedRoute, Params } from '@angular/router';
import { DialougComponent } from '../../shared/dialoug/dialoug.component';
import { MatDialog, MatCheckboxChange } from '@angular/material';

import { DataStorageService } from 'src/app/shared/data-storage.service';
import { RegistrationService } from '../registration.service';
import { SharedService } from 'src/app/shared/shared.service';
import { Applicant } from './dashboard.modal';
import { UserModel } from '../demographic/modal/user.modal';
import { AttributeModel } from '../demographic/modal/attribute.modal';
import { IdentityModel } from '../demographic/modal/identity.modal';
import { FileModel } from '../demographic/modal/file.model';
import { BookingModelRequest } from 'src/app/shared/booking-request.model';
import { RequestModel } from '../demographic/modal/request.modal';
import { DemoIdentityModel } from '../demographic/modal/demo.identity.modal';
import * as appConstants from '../../app.constants';

@Component({
  selector: 'app-registration',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashBoardComponent implements OnInit {
  userFile: FileModel;
  userFiles: any[] = [];
  tempFiles;
  disableModifyDataButton = true;
  disableModifyAppointmentButton = true;
  // numRows: number;
  // numSelected: number;
  fetchedDetails = true;
  modify = false;
  users: Applicant[] = [];
  selectedUsers: Applicant[] = [];
  isNewApplication = false;
  loginId = '';
  isFetched = false;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    public dialog: MatDialog,
    private dataStorageService: DataStorageService,
    private regService: RegistrationService,
    private sharedService: SharedService
  ) {}

  ngOnInit() {
    sessionStorage.clear();
    this.route.params.subscribe((params: Params) => {
      this.loginId = params['id'];
    });
    this.initUsers();
  }

  initUsers() {
    this.regService.flushUsers();
    this.dataStorageService.getUsers(this.loginId).subscribe(
      (applicants: Applicant[]) => {
        console.log('applicant', applicants);
        if (applicants['response'] !== null) {
          sessionStorage.setItem('newApplicant', 'false');
          for (let index = 0; index < applicants['response'].length; index++) {
            const bookingRegistrationDTO = applicants['response'][index]['bookingRegistrationDTO'];
            let appointmentDateTime = '-';
            if (
              bookingRegistrationDTO !== null &&
              applicants['response'][index]['statusCode'].toLowerCase() === appConstants.APPLICATION_STATUS_CODES.booked
            ) {
              const date = applicants['response'][index].bookingRegistrationDTO.reg_date;
              const fromTime = applicants['response'][index].bookingRegistrationDTO.time_slot_from;
              const toTime = applicants['response'][index].bookingRegistrationDTO.time_slot_to;
              appointmentDateTime = date + ' ( ' + fromTime + ' - ' + toTime + ' )';
            }
            const applicant: Applicant = {
              applicationID: applicants['response'][index]['preId'],
              name: applicants['response'][index]['fullname'],
              appointmentDateTime: appointmentDateTime,
              status: applicants['response'][index]['statusCode'],
              regDto: bookingRegistrationDTO
            };
            this.users.push(applicant);
          }
        }
      },
      error => {
        console.log(error);
        // if (error.status < 400) {
        //   console.log('error');
        //   return this.router.navigate(['error']);
        // } else
        if (error.error.err && error.error.err.errorCode === 'PRG_PAM_APP_005') {
          sessionStorage.setItem('newApplicant', 'true');
          this.onNewApplication();
        } else {
          this.router.navigate(['error']);
        }
        this.isFetched = true;
      },
      () => {
        this.isFetched = true;
      }
    );
  }

  onNewApplication() {
    this.router.navigate(['pre-registration', this.loginId, 'demographic']);
    this.isNewApplication = true;
  }

  openDialog(data, width) {
    const dialogRef = this.dialog.open(DialougComponent, {
      width: width,
      data: data
    });
    return dialogRef;
  }

  onDelete(element) {
    let data = {};
    if (element.status.toLowerCase() === 'booked') {
      data = {
        case: 'DISCARD',
        disabled: {
          radioButton1: false,
          radioButton2: false
        }
      };
    } else {
      data = {
        case: 'DISCARD',
        disabled: {
          radioButton1: false,
          radioButton2: true
        }
      };
    }
    let dialogRef = this.openDialog(data, `350px`);
    dialogRef.afterClosed().subscribe(selectedOption => {
      if (selectedOption && Number(selectedOption) === 1) {
        const body = {
          case: 'CONFIRMATION',
          title: 'Confirm',
          message: 'The selected application will be deleted. Please confirm.',
          yesButtonText: 'Confirm',
          noButtonText: 'Cancel'
        };
        dialogRef = this.openDialog(body, '250px');
        dialogRef.afterClosed().subscribe(confirm => {
          if (confirm) {
            this.dataStorageService.deleteRegistration(element.applicationID).subscribe(
              response => {
                const message = {
                  case: 'MESSAGE',
                  title: 'Success',
                  message: 'Action was completed successfully'
                };
                dialogRef = this.openDialog(message, '250px');
                const index = this.users.indexOf(element);
                this.users.splice(index, 1);
                // this.dataSource._updateChangeSubscription();
              },
              error => {
                console.log(error);
                const message = {
                  case: 'MESSAGE',
                  title: 'Error',
                  message: 'Action could not be completed'
                };
                dialogRef = this.openDialog(message, '250px');
              }
            );
          } else {
            const message = {
              case: 'MESSAGE',
              title: 'Error',
              message: 'Action could not be completed'
            };
            dialogRef = this.openDialog(message, '250px');
          }
        });
      } else if (selectedOption && Number(selectedOption) === 2) {
        const body = {
          case: 'CONFIRMATION',
          title: 'Confirm',
          message: 'The selected application will be deleted. Please confirm.',
          yesButtonText: 'Confirm',
          noButtonText: 'Cancel'
        };
        dialogRef = this.openDialog(body, '250px');
        dialogRef.afterClosed().subscribe(confirm => {
          if (confirm) {
            element.regDto.pre_registration_id = element.applicationID;
            this.dataStorageService.cancelAppointment(new BookingModelRequest(element.regDto)).subscribe(
              response => {
                const message = {
                  case: 'MESSAGE',
                  title: 'Success',
                  message: 'Action was completed successfully'
                };
                dialogRef = this.openDialog(message, '250px');
                const index = this.users.indexOf(element);
                // this.dataSource.data[index].status = 'Pending_Appointment';
                // this.dataSource.data[index].appointmentDateTime = '-';
                // this.dataSource._updateChangeSubscription();
              },
              error => {
                console.log(error);
                const message = {
                  case: 'MESSAGE',
                  title: 'Error',
                  message: 'Action could not be completed'
                };
                dialogRef = this.openDialog(message, '250px');
              }
            );
          } else {
            const message = {
              case: 'MESSAGE',
              title: 'Error',
              message: 'Action could not be completed'
            };
            dialogRef = this.openDialog(message, '250px');
          }
        });
      }
    });
  }

  onModifyInformation(preId: string) {
    this.disableModifyDataButton = true;
    this.dataStorageService.getUserDocuments(preId).subscribe(
      response => {
        this.setUserFiles(response);
      },
      error => {},
      () => {
        this.dataStorageService.getUser(preId).subscribe(
          response => {
            const request = this.createRequestJSON(response['response'][0]);
            this.disableModifyDataButton = true;
            this.regService.addUser(new UserModel(preId, request, this.userFiles));
          },
          error => {
            console.log('error', error);
            this.disableModifyDataButton = false;
            this.fetchedDetails = true;
            return this.router.navigate(['error']);
          },
          () => {
            this.fetchedDetails = true;
            this.router.navigate(['pre-registration', this.loginId, 'demographic']);
          }
        );
      }
    );
  }

  onSelectUser(user: Applicant, event?: MatCheckboxChange) {
    if (!event && user) {
      this.selectedUsers.length = 0;
      this.selectedUsers.push(user);
    } else if (event && event.checked) {
      this.selectedUsers.push(user);
    } else {
      this.selectedUsers.splice(this.selectedUsers.indexOf(user));
    }

    if (this.selectedUsers.length > 0) {
      this.disableModifyAppointmentButton = false;
    } else {
      this.disableModifyAppointmentButton = true;
    }
    console.log(this.selectedUsers);
  }

  onModifyMultipleAppointment() {
    for (let index = 0; index < this.selectedUsers.length; index++) {
      const preId = this.selectedUsers[index].applicationID;
      const fullName = this.selectedUsers[index].name;
      const regDto = this.selectedUsers[index].regDto;
      const status = this.selectedUsers[index].status;
      this.sharedService.addNameList({
        fullName: fullName,
        preRegId: preId,
        regDto: regDto,
        status: status
      });
    }
    this.router.navigate(['../../', 'pre-registration', this.loginId, 'pick-center'], { relativeTo: this.route });
  }
  private createIdentityJSON(identityModal: IdentityModel) {
    const identity = new IdentityModel(
      [
        new AttributeModel(
          identityModal.fullName[0].language,
          identityModal.fullName[0].label,
          identityModal.fullName[0].value
        ),
        new AttributeModel(
          identityModal.fullName[1].language,
          identityModal.fullName[1].label,
          identityModal.fullName[1].value
        )
      ],
      [
        new AttributeModel(
          identityModal.dateOfBirth[0].language,
          identityModal.dateOfBirth[0].label,
          identityModal.dateOfBirth[0].value
        ),
        new AttributeModel(
          identityModal.dateOfBirth[1].language,
          identityModal.dateOfBirth[1].label,
          identityModal.dateOfBirth[1].value
        )
      ],
      [
        new AttributeModel(
          identityModal.gender[0].language,
          identityModal.gender[0].label,
          identityModal.gender[0].value
        ),
        new AttributeModel(
          identityModal.gender[1].language,
          identityModal.gender[1].label,
          identityModal.gender[1].value
        )
      ],
      [
        new AttributeModel(
          identityModal.addressLine1[0].language,
          identityModal.addressLine1[0].label,
          identityModal.addressLine1[0].value
        ),
        new AttributeModel(
          identityModal.addressLine1[1].language,
          identityModal.addressLine1[1].label,
          identityModal.addressLine1[1].value
        )
      ],
      [
        new AttributeModel(
          identityModal.addressLine2[0].language,
          identityModal.addressLine2[0].label,
          identityModal.addressLine2[0].value
        ),
        new AttributeModel(
          identityModal.addressLine2[1].language,
          identityModal.addressLine2[1].label,
          identityModal.addressLine2[1].value
        )
      ],
      [
        new AttributeModel(
          identityModal.addressLine3[0].language,
          identityModal.addressLine3[0].label,
          identityModal.addressLine3[0].value
        ),
        new AttributeModel(
          identityModal.addressLine3[1].language,
          identityModal.addressLine3[1].label,
          identityModal.addressLine3[1].value
        )
      ],
      [
        new AttributeModel(
          identityModal.region[0].language,
          identityModal.region[0].label,
          identityModal.region[0].value
        ),
        new AttributeModel(
          identityModal.region[1].language,
          identityModal.region[1].label,
          identityModal.region[1].value
        )
      ],
      [
        new AttributeModel(
          identityModal.province[0].language,
          identityModal.province[0].label,
          identityModal.province[0].value
        ),
        new AttributeModel(
          identityModal.province[1].language,
          identityModal.province[1].label,
          identityModal.province[1].value
        )
      ],
      [
        new AttributeModel(identityModal.city[0].language, identityModal.city[0].label, identityModal.city[0].value),
        new AttributeModel(identityModal.city[1].language, identityModal.city[1].label, identityModal.city[1].value)
      ],
      [
        new AttributeModel(
          identityModal.localAdministrativeAuthority[0].language,
          identityModal.localAdministrativeAuthority[0].label,
          identityModal.localAdministrativeAuthority[0].value
        ),
        new AttributeModel(
          identityModal.localAdministrativeAuthority[1].language,
          identityModal.localAdministrativeAuthority[1].label,
          identityModal.localAdministrativeAuthority[1].value
        )
      ],
      [
        new AttributeModel(
          identityModal.postalcode[0].language,
          identityModal.postalcode[0].label,
          identityModal.postalcode[0].value
        ),
        new AttributeModel(
          identityModal.postalcode[1].language,
          identityModal.postalcode[1].label,
          identityModal.postalcode[1].value
        )
      ],
      [
        new AttributeModel(
          identityModal.mobileNumber[0].language,
          identityModal.mobileNumber[0].label,
          identityModal.mobileNumber[0].value
        ),
        new AttributeModel(
          identityModal.mobileNumber[1].language,
          identityModal.mobileNumber[1].label,
          identityModal.mobileNumber[1].value
        )
      ],
      [
        new AttributeModel(
          identityModal.emailId[0].language,
          identityModal.emailId[0].label,
          identityModal.emailId[0].value
        ),
        new AttributeModel(
          identityModal.emailId[1].language,
          identityModal.emailId[1].label,
          identityModal.emailId[1].value
        )
      ],
      [
        new AttributeModel(
          identityModal.CNEOrPINNumber[0].language,
          identityModal.CNEOrPINNumber[0].label,
          identityModal.CNEOrPINNumber[0].value
        ),
        new AttributeModel(
          identityModal.CNEOrPINNumber[1].language,
          identityModal.CNEOrPINNumber[1].label,
          identityModal.CNEOrPINNumber[1].value
        )
      ]
    );

    return identity;
  }

  private createRequestJSON(requestModal: RequestModel) {
    const identity = this.createIdentityJSON(requestModal.demographicDetails.identity);
    const req: RequestModel = {
      preRegistrationId: requestModal.preRegistrationId,
      createdBy: requestModal.createdBy,
      createdDateTime: requestModal.createdDateTime,
      updatedBy: this.loginId,
      updatedDateTime: '',
      statusCode: requestModal.statusCode,
      langCode: requestModal.langCode,
      demographicDetails: new DemoIdentityModel(identity)
    };
    return req;
  }

  setUserFiles(response) {
    console.log('user files fetched', response);
    this.userFile = response.response;
    this.userFiles.push(this.userFile);
    console.log('user files after pushing', this.userFiles);
  }

  getColor(value: string) {
    if (value === appConstants.APPLICATION_STATUS_CODES.pending) return 'orange';
    if (value === appConstants.APPLICATION_STATUS_CODES.booked) return 'green';
    if (value === appConstants.APPLICATION_STATUS_CODES.expired) return 'red';
  }
}
