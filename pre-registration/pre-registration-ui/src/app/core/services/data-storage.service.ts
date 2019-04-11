import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

import * as appConstants from '../../app.constants';
import { AppConfigService } from '../../app-config.service';
import { Applicant } from '../../shared/models/dashboard-model/dashboard.modal';
import { ConfigService } from './config.service';
import { RequestModel } from 'src/app/shared/models/request-model/RequestModel';

@Injectable({
  providedIn: 'root'
})
export class DataStorageService {
  constructor(
    private httpClient: HttpClient,
    private appConfigService: AppConfigService,
    private configService: ConfigService
  ) {}

  //need to remove
  // BASE_URL = 'https://dev.mosip.io/';
  // BASE_URL_LOCAL = 'http://A2ML29862:9092/demographic/applications';

  BASE_URL = this.appConfigService.getConfig()['BASE_URL'];
  // BASE_URL = 'https://dev.mosip.io/';
  PRE_REG_URL = this.appConfigService.getConfig()['PRE_REG_URL'];

  getUsers(userId: string) {
    let url = this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.applicants;
    return this.httpClient.get<Applicant[]>(url);
  }

  getUser(preRegId: string) {
    let url = this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.applicants + appConstants.APPENDER + preRegId;
    return this.httpClient.get(url);
  }

  getGenderDetails() {
    const url = this.BASE_URL + appConstants.APPEND_URL.gender;
    return this.httpClient.get(url);
  }

  getTransliteration(request: any) {
    const obj = new RequestModel(appConstants.IDS.transliteration, request);
    const url = this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.transliteration;
    return this.httpClient.post(url, obj);
  }

  getUserDocuments(preRegId) {
    console.log('documents fetched for : ', preRegId);

    return this.httpClient.get(
      this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.document + preRegId
      //   ,{
      //   observe: 'body',
      //   responseType: 'json',
      //   params: new HttpParams().append(appConstants.PARAMS_KEYS.getDocument, preRegId)
      // }
    );
  }

  addUser(identity: any) {
    const obj = new RequestModel(appConstants.IDS.newUser, identity);
    let url = this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.applicants;
    console.log('data being sent', JSON.stringify(obj));
    return this.httpClient.post(url, obj);
  }

  updateUser(identity: any, preRegId: string) {
    let url = this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.applicants + appConstants.APPENDER + preRegId;
    const obj = new RequestModel(appConstants.IDS.updateUser, identity);
    console.log('data being update', JSON.stringify(obj));
    return this.httpClient.put(url, obj);
  }

  sendFile(formdata: FormData, preRegId) {
    console.log('formData', formdata);

    return this.httpClient.post(
      this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.document + preRegId,
      formdata
    );
    // console.log('servvice called', formdata);
  }

  deleteRegistration(preId: string) {
    // const url = this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.applicants + appConstants.APPENDER + preId;
    return this.httpClient.delete(
      this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.delete_application + preId
    );
  }

  cancelAppointment(data: RequestModel, preRegId: string) {
    console.log('cancel appointment data', data);
    return this.httpClient.put(
      this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.cancelAppointment + preRegId,
      data
    );
  }

  getNearbyRegistrationCenters(coords: any) {
    return this.httpClient.get(
      this.BASE_URL +
        appConstants.APPEND_URL.master_data +
        appConstants.APPEND_URL.nearby_registration_centers +
        localStorage.getItem('langCode') +
        '/' +
        coords.longitude +
        '/' +
        coords.latitude +
        '/' +
        this.configService.getConfigByKey(appConstants.CONFIG_KEYS.preregistration_nearby_centers)
    );
  }

  getRegistrationCentersByName(locType: string, text: string) {
    return this.httpClient.get(
      this.BASE_URL +
        appConstants.APPEND_URL.master_data +
        appConstants.APPEND_URL.registration_centers_by_name +
        localStorage.getItem('langCode') +
        '/' +
        locType +
        '/' +
        text
    );
  }

  getLocationTypeData() {
    return this.httpClient.get(
      this.BASE_URL + appConstants.APPEND_URL.master_data + 'locations/' + localStorage.getItem('langCode')
    );
  }

  getAvailabilityData(registrationCenterId) {
    return this.httpClient.get(
      this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.booking_availability + registrationCenterId
    );
  }

  makeBooking(request: RequestModel) {
    console.log('request inside service', request);
    return this.httpClient.post(
      this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.booking_appointment,
      request
    );
  }

  getLocationMetadataHirearchy(value: string) {
    const url = this.BASE_URL + appConstants.APPEND_URL.location + appConstants.APPEND_URL.location_metadata + value;
    return this.httpClient.get(url);
  }

  getLocationImmediateHierearchy(lang: string, location: string) {
    const url =
      this.BASE_URL +
      appConstants.APPEND_URL.location +
      appConstants.APPEND_URL.location_immediate_children +
      location +
      appConstants.APPENDER +
      lang;
    return this.httpClient.get(url);
  }

  deleteFile(documentId) {
    return this.httpClient.delete(this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.document, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append(appConstants.PARAMS_KEYS.deleteFile, documentId)
    });
  }

  getSecondaryLanguageLabels(langCode: string) {
    return this.httpClient.get(`./assets/i18n/${langCode}.json`);
  }

  copyDocument(sourceId: string, destinationId: string) {
    const url = this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.document + destinationId;
    console.log('copy document URL', url);
    return this.httpClient.put(url, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams()
        .append(appConstants.PARAMS_KEYS.catCode, appConstants.PARAMS_KEYS.POA)
        .append(appConstants.PARAMS_KEYS.sourcePrId, sourceId)
    });
  }

  generateQRCode(data: string) {
    const obj = new RequestModel(appConstants.IDS.qrCode, data);
    return this.httpClient.post(this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.qr_code, obj);
  }

  sendNotification(data: FormData) {
    return this.httpClient.post(
      this.BASE_URL +
        this.PRE_REG_URL +
        appConstants.APPEND_URL.notification +
        appConstants.APPEND_URL.send_notification,
      data
    );
  }

  recommendedCenters(langCode: string, locationHierarchyCode: number, data: string[]) {
    let url =
      this.BASE_URL +
      appConstants.APPEND_URL.master_data +
      'registrationcenters/' +
      langCode +
      '/' +
      locationHierarchyCode +
      '/names?';
    data.forEach(name => {
      url += 'name=' + name;
      if (data.indexOf(name) !== data.length - 1) {
        url += '&';
      }
    });
    if (url.charAt(url.length - 1) === '&') {
      url = url.substring(0, url.length - 1);
    }
    console.log(url);
    return this.httpClient.get(url);
  }

  getRegistrationCenterByIdAndLangCode(id: string, langCode: string) {
    const url = this.BASE_URL + appConstants.APPEND_URL.master_data + 'registrationcenters/' + id + '/' + langCode;
    return this.httpClient.get(url);
  }

  getGuidelineTemplate() {
    const url =
      this.BASE_URL +
      appConstants.APPEND_URL.master_data +
      'templates/' +
      localStorage.getItem('langCode') +
      '/' +
      'Onscreen-Acknowledgement';
    return this.httpClient.get(url);
  }

  getApplicantType(docuemntCategoryDto) {
    return this.httpClient.post(
      this.BASE_URL + appConstants.APPEND_URL.applicantType + appConstants.APPEND_URL.getApplicantType,
      docuemntCategoryDto
    );
  }

  getDocumentCategories(applicantCode) {
    const APPLICANT_VALID_DOCUMENTS_URL =
      this.BASE_URL +
      appConstants.APPEND_URL.location +
      appConstants.APPEND_URL.validDocument +
      applicantCode +
      '/languages';
    return this.httpClient.get(APPLICANT_VALID_DOCUMENTS_URL, {
      params: new HttpParams().append(appConstants.PARAMS_KEYS.getDocumentCategories, localStorage.getItem('langCode'))
    });
  }

  getConfig() {
    //    return this.httpClient.get('./assets/configs.json');
    const url = this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.auth + appConstants.APPEND_URL.config;
    return this.httpClient.get(url);
  }

  sendOtp(userId: string) {
    console.log(userId);

    const req = {
      langCode: localStorage.getItem('langCode'),
      userId: userId
    };

    const obj = new RequestModel(appConstants.IDS.sendOtp, req);

    const url = this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.auth + appConstants.APPEND_URL.send_otp;
    return this.httpClient.post(url, obj);
  }

  verifyOtp(userId: string, otp: string) {
    const request = {
      otp: otp,
      userId: userId
    };

    const obj = new RequestModel(appConstants.IDS.validateOtp, request);

    const url = this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.auth + appConstants.APPEND_URL.login;
    return this.httpClient.post(url, obj);
  }

  onLogout() {
    const url = this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.auth + appConstants.APPEND_URL.logout;
    return this.httpClient.post(url, '');
  }
}
