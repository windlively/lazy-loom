import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FileModel {
  reqPath: string;
  name: string;
  dir: boolean;
  lastModified: string;
  spaces: number;
}

export interface HttpResult<T> {
  message: string | null;
  code: string;
  success: boolean;
  data: T;
}

const BASE = '/file-server';

@Injectable({ providedIn: 'root' })
export class FileService {

  constructor(private http: HttpClient) {}

  listFiles(path: string = '/'): Observable<HttpResult<FileModel[]>> {
    const params = new HttpParams().set('path', path);
    return this.http.get<HttpResult<FileModel[]>>(`${BASE}/api/list`, { params });
  }

  deleteFile(path: string): Observable<HttpResult<string>> {
    const params = new HttpParams().set('path', path);
    return this.http.delete<HttpResult<string>>(`${BASE}/api/delete`, { params });
  }

  getDownloadUrl(path: string): string {
    return `${BASE}/api/download?path=${encodeURIComponent(path)}`;
  }

  uploadChunk(fileId: string, offset: number, chunk: Blob, fileName: string): Observable<HttpResult<number>> {
    const fd = new FormData();
    fd.append('fileId', fileId);
    fd.append('offset', String(offset));
    fd.append('chunk', chunk, fileName);
    return this.http.post<HttpResult<number>>(`${BASE}/upload/chunk`, fd);
  }

  getUploadProgress(fileId: string): Observable<HttpResult<number>> {
    const params = new HttpParams().set('fileId', fileId);
    return this.http.get<HttpResult<number>>(`${BASE}/upload/progress`, { params });
  }

  completeUpload(fileId: string, fileName: string, path: string, saveAsOriginalName: boolean, totalSize: number): Observable<HttpResult<string>> {
    const fd = new FormData();
    fd.append('fileId', fileId);
    fd.append('fileName', fileName);
    fd.append('path', path);
    fd.append('saveAsOriginalName', String(saveAsOriginalName));
    fd.append('totalSize', String(totalSize));
    return this.http.post<HttpResult<string>>(`${BASE}/upload/complete`, fd);
  }

  abortUpload(fileId: string): Observable<HttpResult<string>> {
    const fd = new FormData();
    fd.append('fileId', fileId);
    return this.http.post<HttpResult<string>>(`${BASE}/upload/abort`, fd);
  }
}
