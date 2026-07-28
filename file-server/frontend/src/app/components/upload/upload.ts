import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormsModule } from '@angular/forms';
import { FileService } from '../../services/file.service';

interface UploadTask {
  id: string;
  file: File;
  uploadedBytes: number;
  status: 'uploading' | 'paused' | 'completing' | 'done' | 'error' | 'cancelled';
  errorMsg: string | null;
}

const CHUNK_SIZE = 1024 * 1024; // 1MB

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [
    CommonModule, RouterLink, FormsModule,
    MatToolbarModule, MatIconModule, MatButtonModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatCheckboxModule, MatProgressBarModule, MatSnackBarModule,
    MatTooltipModule
  ],
  templateUrl: './upload.html',
  styleUrl: './upload.scss'
})
export class UploadComponent {
  tasks = signal<UploadTask[]>([]);
  uploadPath = '';
  saveAsOriginalName = true;
  dragover = signal(false);

  constructor(private fileService: FileService, private snackBar: MatSnackBar) {}

  get doneCount(): number {
    return this.tasks().filter(t => t.status === 'done').length;
  }

  get hasUploading(): boolean {
    return this.tasks().some(t => t.status === 'uploading' || t.status === 'completing');
  }

  get hasPaused(): boolean {
    return this.tasks().some(t => t.status === 'paused' || t.status === 'error');
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files) this.addFiles(input.files);
    input.value = '';
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.dragover.set(false);
    if (event.dataTransfer?.files) this.addFiles(event.dataTransfer.files);
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.dragover.set(true);
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.dragover.set(false);
  }

  private addFiles(fileList: FileList) {
    const newTasks: UploadTask[] = [];
    for (let i = 0; i < fileList.length; i++) {
      const task: UploadTask = {
        id: this.uuid(),
        file: fileList[i],
        uploadedBytes: 0,
        status: 'uploading',
        errorMsg: null
      };
      newTasks.push(task);
    }
    this.tasks.update(t => [...t, ...newTasks]);
    newTasks.forEach(t => this.pumpChunk(t));
  }

  private pumpChunk(task: UploadTask) {
    if (task.status !== 'uploading') return;
    if (task.uploadedBytes >= task.file.size) {
      this.completeUpload(task);
      return;
    }
    const start = task.uploadedBytes;
    const end = Math.min(start + CHUNK_SIZE, task.file.size);
    const blob = task.file.slice(start, end);

    this.fileService.uploadChunk(task.id, start, blob, task.file.name).subscribe({
      next: (res) => {
        if (task.status !== 'uploading') return;
        if (res.success) {
          task.uploadedBytes = res.data;
          this.tasks.update(t => [...t]);
          this.pumpChunk(task);
        } else {
          this.failTask(task, res.message || '上传失败');
        }
      },
      error: () => {
        if (task.status === 'uploading') this.failTask(task, '网络错误');
      }
    });
  }

  private completeUpload(task: UploadTask) {
    task.status = 'completing';
    this.tasks.update(t => [...t]);
    this.fileService.completeUpload(
      task.id, task.file.name, this.uploadPath, this.saveAsOriginalName, task.file.size
    ).subscribe({
      next: (res) => {
        if (res.success) {
          task.status = 'done';
          task.uploadedBytes = task.file.size;
          this.snackBar.open(`${task.file.name} 上传成功`, '关闭', { duration: 3000 });
        } else {
          task.status = 'error';
          task.errorMsg = res.message || '合并失败';
        }
        this.tasks.update(t => [...t]);
      },
      error: () => {
        task.status = 'error';
        task.errorMsg = '网络错误';
        this.tasks.update(t => [...t]);
      }
    });
  }

  private failTask(task: UploadTask, msg: string) {
    task.status = 'error';
    task.errorMsg = msg;
    this.tasks.update(t => [...t]);
  }

  toggleTask(task: UploadTask) {
    if (task.status === 'uploading') {
      task.status = 'paused';
      this.tasks.update(t => [...t]);
    } else if (task.status === 'paused' || task.status === 'error') {
      task.status = 'uploading';
      task.errorMsg = null;
      this.tasks.update(t => [...t]);
      this.fileService.getUploadProgress(task.id).subscribe({
        next: (res) => {
          if (task.status !== 'uploading') return;
          if (res.success) task.uploadedBytes = res.data || 0;
          this.tasks.update(t => [...t]);
          this.pumpChunk(task);
        },
        error: () => {
          if (task.status === 'uploading') this.pumpChunk(task);
        }
      });
    }
  }

  cancelTask(task: UploadTask) {
    task.status = 'cancelled';
    this.fileService.abortUpload(task.id).subscribe();
    this.tasks.update(t => t.filter(x => x.id !== task.id));
  }

  pauseAll() {
    this.tasks().forEach(t => {
      if (t.status === 'uploading') t.status = 'paused';
    });
    this.tasks.update(t => [...t]);
  }

  resumeAll() {
    const toResume = this.tasks().filter(t => t.status === 'paused' || t.status === 'error');
    toResume.forEach(t => this.toggleTask(t));
  }

  getPercent(task: UploadTask): number {
    if (task.file.size === 0) return 100;
    return Math.min(100, Math.floor(task.uploadedBytes / task.file.size * 100));
  }

  getProgressText(task: UploadTask): string {
    const pct = this.getPercent(task);
    return `${pct}% (${this.formatSize(Math.min(task.uploadedBytes, task.file.size))} / ${this.formatSize(task.file.size)})`;
  }

  getStatusText(task: UploadTask): string {
    const map: Record<string, string> = {
      uploading: '上传中',
      paused: '已暂停',
      completing: '合并中…',
      done: '已完成',
      error: '失败：' + (task.errorMsg || '')
    };
    return map[task.status] || '';
  }

  getProgressColor(task: UploadTask): string {
    if (task.status === 'done') return 'primary';
    if (task.status === 'error') return 'warn';
    return 'primary';
  }

  getProgressMode(task: UploadTask): string {
    return 'determinate';
  }

  formatSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
    if (bytes < 1073741824) return (bytes / 1048576).toFixed(2) + ' MB';
    return (bytes / 1073741824).toFixed(2) + ' GB';
  }

  private uuid(): string {
    return 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'.replace(/[x]/g, () =>
      (Math.random() * 16 | 0).toString(16)
    );
  }
}
