{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "batch.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default short app name to use for resource naming.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "batch.fullname" -}}
{{- $name := default "batch-process" .Values.appNameOverride -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create an uppercase app name to use for environment variables.
*/}}
{{- define "batch.envname" -}}
{{- $name := default "batch-process" .Values.appNameOverride -}}
{{- printf "%s_%s" .Release.Name $name | upper | replace "-" "_" | trimSuffix "_" -}}
{{- end -}}

{{/*
Create a name to use for datasource variables.
*/}}
{{- define "batch.database" -}}
{{- if .Values.datasource.host -}}
    {{ default "default" .Values.datasource.host }}
{{- else -}}
    {{- printf "%s-%s" .Release.Name .Values.datasource.type | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{/*
Create an uppercase release prefix to use for environment variables.
*/}}
{{- define "batch.envrelease" -}}
{{- printf "%s" .Release.Name | upper | replace "-" "_" | trimSuffix "_" -}}
{{- end -}}

{{/*
Create the name of the service account to use
*/}}
{{- define "batch.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
    {{ default (include "batch.fullname" .) .Values.serviceAccount.name }}
{{- else -}}
    {{ default "default" .Values.serviceAccount.name }}
{{- end -}}
{{- end -}}
