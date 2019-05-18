export const menuItems = [
  'Master Data',
  'Assets',
  'Users',
  'UIN',
  'Configuration'
];

export const appId = 'admin';
export const applicationVersion = 'v1';
export const userIdType = 'USERID';
export const loginOtpContext = 'auth-otp';




export const base_url = 'dev.mosip.io';
export const masterData_base_url = `https://${base_url}/v1/masterdata/`;
export const code_url_mapping = {
  'centers': {
    appendURL: `registrationcenters`,
    parameterName: 'registrationCenters'
  },
  'biometric-types': {
    appendURL: `biometrictypes`,
    parameterName: 'biometrictypes'
  },
  'individuals': {
    appendURL: 'individualtypes',
    parameterName: 'individualTypes'
  },
  'idtypes': {
    appendURL: 'idtypes/eng',
    parameterName: 'idtypes'
  },
  'holidays': {
    appendURL: 'holidays',
    parameterName: 'holidays'
  },
  'rejection-reason': {
    appendURL: 'packetrejectionreasons',
    parameterName: 'reasonCategories'
  },
  'devices': {
    appendURL: 'devices/eng',
    parameterName: 'devices'
  },
  'device-specs': {
    appendURL: 'devicespecifications/eng',
    parameterName: 'devicespecifications'
  },
  'templates': {
    appendURL: 'templates',
    parameterName: 'templates'
  },
  'genders': {
    appendURL: 'gendertypes',
    parameterName: 'genderType'
  },
  'titles': {
    appendURL: 'title',
    parameterName: ''
  },
  'document-category': {
    appendURL: 'documentcategories',
    parameterName: 'documentcategories'
  },
  'blacklisted': {
    appendURL: 'blacklistedwords/eng',
    parameterName: 'blacklistedwords'
  },
  'locations': {
    appendURL: 'locations/eng',
    parameterName: 'locations'
  },
  'machines': {
    appendURL: 'machines',
    parameterName: 'machines'
  },
  'applications': {
    appendURL: 'applicationtypes',
    parameterName: 'applicationtypes'
  },
  'valid-document': {
    appendURL: 'validdocuments/eng',
    parameterName: 'documentcategories'
  }
};

export const admin_base_url = `https://${base_url}/v1/admin/`;

export const loginURL = {
  userRole: `${admin_base_url}security/authfactors/`,
  userIdpasswd: `${admin_base_url}useridPwd`,
  sendOtp: `https://${base_url}/v1/authmanager/authenticate/sendotp`,
  verifyOtp: `https://${base_url}/v1/authmanager/authenticate/useridOTP`
};

