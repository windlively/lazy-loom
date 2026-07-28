import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FileService, FileModel } from '../../services/file.service';

@Component({
  selector: 'app-explorer',
  standalone: true,
  imports: [
    CommonModule, RouterLink,
    MatToolbarModule, MatIconModule, MatButtonModule,
    MatTableModule, MatCardModule, MatButtonToggleModule,
    MatTooltipModule, MatSnackBarModule, MatProgressSpinnerModule
  ],
  templateUrl: './explorer.html',
  styleUrl: './explorer.scss'
})
export class ExplorerComponent implements OnInit {
  files = signal<FileModel[]>([]);
  currentPath = signal<string>('/');
  viewMode = signal<'list' | 'grid'>('list');
  loading = signal(false);
  error = signal<string | null>(null);

  displayedColumns = ['name', 'lastModified', 'size', 'actions'];

  breadcrumbs = computed(() => {
    const path = this.currentPath();
    const segments = path.split('/').filter(s => s !== '');
    const crumbs: { name: string; path: string }[] = [];
    let acc = '';
    for (const seg of segments) {
      acc += '/' + seg;
      crumbs.push({ name: seg, path: acc });
    }
    return crumbs;
  });

  parentPath = computed(() => {
    const path = this.currentPath();
    if (path === '/') return null;
    const idx = path.lastIndexOf('/');
    return idx <= 0 ? '/' : path.substring(0, idx);
  });

  constructor(private fileService: FileService, private snackBar: MatSnackBar) {
    const saved = localStorage.getItem('fs-view-mode');
    if (saved === 'grid') this.viewMode.set('grid');
  }

  ngOnInit() {
    this.loadFiles('/');
  }

  loadFiles(path: string) {
    this.loading.set(true);
    this.error.set(null);
    this.fileService.listFiles(path).subscribe({
      next: (res) => {
        if (res.success) {
          this.files.set(res.data || []);
          this.currentPath.set(path);
        } else {
          this.error.set(res.message || '加载失败');
        }
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('请求失败: ' + err.message);
        this.loading.set(false);
      }
    });
  }

  navigate(file: FileModel) {
    if (file.dir) {
      this.loadFiles(file.reqPath);
    }
  }

  goUp() {
    const parent = this.parentPath();
    if (parent) this.loadFiles(parent);
  }

  goToBreadcrumb(path: string) {
    this.loadFiles(path);
  }

  goRoot() {
    this.loadFiles('/');
  }

  deleteFile(file: FileModel, event: Event) {
    event.preventDefault();
    event.stopPropagation();
    if (!confirm(`确定要删除 "${file.name}" 吗？此操作不可恢复！`)) return;
    this.fileService.deleteFile(file.reqPath).subscribe({
      next: (res) => {
        if (res.success) {
          this.snackBar.open(`已删除: ${file.name}`, '关闭', { duration: 3000 });
          this.loadFiles(this.currentPath());
        } else {
          this.snackBar.open(res.message || '删除失败', '关闭', { duration: 3000 });
        }
      },
      error: () => this.snackBar.open('删除请求失败', '关闭', { duration: 3000 })
    });
  }

  setView(mode: 'list' | 'grid') {
    this.viewMode.set(mode);
    localStorage.setItem('fs-view-mode', mode);
  }

  getDownloadUrl(file: FileModel): string {
    return this.fileService.getDownloadUrl(file.reqPath);
  }

  formatSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
    if (bytes < 1073741824) return (bytes / 1048576).toFixed(2) + ' MB';
    return (bytes / 1073741824).toFixed(2) + ' GB';
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '-';
    const d = new Date(dateStr);
    return d.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });
  }
}

