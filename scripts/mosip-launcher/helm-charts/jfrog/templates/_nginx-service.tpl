{{- if .Values.nginx.enabled -}}
apiVersion: v1
kind: Service
metadata:
  name: {{ template "artifactory.nginx.fullname" . }}
  labels:
    app: {{ template "artifactory.name" . }}
    chart: {{ template "artifactory.chart" . }}
    component: {{ .Values.nginx.name }}
    heritage: {{ .Release.Service }}
    release: {{ .Release.Name }}
{{- if .Values.nginx.service.annotations }}
  annotations:
{{ toYaml .Values.nginx.service.annotations | indent 4 }}
{{- end }}
spec:
  type: {{ .Values.nginx.service.type }}
{{- if eq .Values.nginx.service.type "LoadBalancer" }}
  {{ if .Values.nginx.service.loadBalancerIP -}}
  loadBalancerIP: {{ .Values.nginx.service.loadBalancerIP }}
  {{ end -}}
  {{- if .Values.nginx.service.externalTrafficPolicy }}
  externalTrafficPolicy: {{ .Values.nginx.service.externalTrafficPolicy }}
  {{- end }}
{{- end }}
{{- if .Values.nginx.service.loadBalancerSourceRanges }}
  loadBalancerSourceRanges:
{{ toYaml .Values.nginx.service.loadBalancerSourceRanges | indent 4 }}
{{- end }}
  ports:
  {{- if .Values.artifactory.replicator.enabled }}
  - port: {{ .Values.nginx.externalPortReplicator }}
    targetPort: {{ .Values.nginx.internalPortReplicator }}
    protocol: TCP
    name: replicator
  {{- end }}
  - port: {{ .Values.nginx.externalPortHttp }}
    targetPort: {{ .Values.nginx.internalPortHttp }}
    protocol: TCP
    name: {{ .Values.nginx.name }}http
  - port: {{ .Values.nginx.externalPortHttps }}
    targetPort: {{ .Values.nginx.internalPortHttps }}
    protocol: TCP
    name: {{ .Release.Name }}https
  selector:
    app: {{ template "artifactory.name" . }}
    component: {{ .Values.nginx.name }}
    release: {{ .Release.Name }}
{{- end }}
