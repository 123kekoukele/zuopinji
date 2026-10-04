<template>
  <div class="xuantishenqing-page">
    <!-- 筛选与操作区 -->
    <div class="filter-card">
      <!-- 筛选条件行 -->
      <div class="filter-row">
        <div class="filter-group">
          <el-input
            v-model="filter.timubianhao"
            placeholder="题目编号"
            clearable
            class="filter-input"
            @keyup.enter="handleFilter"
            @clear="handleFilter"
          >
            <template #prefix><el-icon><Document /></el-icon></template>
          </el-input>

          <el-input
            v-model="filter.ketimingcheng"
            placeholder="课题名称"
            clearable
            class="filter-input filter-input-wide"
            @keyup.enter="handleFilter"
            @clear="handleFilter"
          >
            <template #prefix><el-icon><Collection /></el-icon></template>
          </el-input>

          <el-select
            v-model="filter.shenhezhuangtai"
            placeholder="审核状态"
            clearable
            class="filter-input"
            @change="handleFilter"
          >
            <el-option label="未审核" value="未审核" />
            <el-option label="通过" value="通过" />
            <el-option label="驳回" value="驳回" />
          </el-select>

          <el-input
            v-model="filter.xueshengxingming"
            placeholder="学生姓名"
            clearable
            class="filter-input"
            @keyup.enter="handleFilter"
            @clear="handleFilter"
          >
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>

          <el-input
            v-model="filter.jiaoshixingming"
            placeholder="指导教师"
            clearable
            class="filter-input"
            :disabled="isTeacherRole"
            @keyup.enter="handleFilter"
            @clear="handleFilter"
          >
            <template #prefix><el-icon><Avatar /></el-icon></template>
          </el-input>

          <el-select
            v-model="filter.zhuanye"
            placeholder="专业"
            clearable
            class="filter-input"
            @change="handleFilter"
          >
            <el-option
              v-for="item in zhuanyeOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>

          <el-select
            v-model="filter.timuleixing"
            placeholder="题目类型"
            clearable
            class="filter-input"
            @change="handleFilter"
          >
            <el-option
              v-for="item in timuleixingOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </div>

        <div class="filter-actions">
          <el-button type="primary" class="search-btn" @click="handleFilter">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button class="reset-btn" @click="handleReset">
            <el-icon><RefreshRight /></el-icon>重置
          </el-button>
        </div>
      </div>

      <!-- 操作栏 -->
      <div class="action-row">
        <div class="action-left">
          <template v-if="canOperate">
            <el-button class="btn-pass-all" :disabled="!selectedRows.length" @click="openBatchPass">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" style="margin-right:4px">
              <path d="M5 13l4 4L19 7" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            批量通过
          </el-button>
          <el-button class="btn-reject-all" :disabled="!selectedRows.length" @click="openBatchReject">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" style="margin-right:4px">
              <path d="M6 18L18 6M6 6l12 12" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
            </svg>
            批量驳回
          </el-button>
          <el-button class="btn-delete-all" :disabled="!selectedRows.length" @click="handleDelete()">
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
          </template>
          <span v-else class="readonly-tip">当前为管理员账号，仅可查看选题申请</span>
          <el-button class="btn-export" @click="handleExport">
            <el-icon><Download /></el-icon>导出Excel
          </el-button>
        </div>
        <div class="action-right" v-if="canOperate && selectedRows.length > 0">
          <span class="selected-tip">
            <el-icon><Check /></el-icon>
            已选择 <strong>{{ selectedRows.length }}</strong> 项
          </span>
          <el-button size="small" text @click="clearSelection">清空</el-button>
        </div>
      </div>
    </div>

    <!-- 表格区域 -->
    <div class="table-card">
      <el-table
        :data="filteredData"
        v-loading="loading"
        ref="tableRef"
        row-key="id"
        :header-cell-style="headerCellStyle"
        :row-class-name="rowClassName"
        @selection-change="handleSelectionChange"
        @row-click="handleRowClick"
        class="shenqing-table"
      >
        <!-- 多选 -->
        <el-table-column v-if="canOperate" type="selection" width="50" align="center" />

        <!-- 序号 -->
        <el-table-column label="序号" width="60" align="center">
          <template #default="scope">
            <span class="row-index">{{ scope.$index + 1 }}</span>
          </template>
        </el-table-column>

        <!-- 课题名称 -->
        <el-table-column label="课题名称" min-width="280" show-overflow-tooltip>
          <template #default="scope">
            <div class="topic-name-cell" @click.stop="openDetail(scope.row)">
              <el-tooltip
                :content="scope.row.ketimingcheng"
                placement="top"
                :disabled="!scope.row.ketimingcheng || scope.row.ketimingcheng.length <= 18"
              >
                <span class="topic-name-text">{{ scope.row.ketimingcheng }}</span>
              </el-tooltip>
              <el-tag
                v-if="isTopicDeleted(scope.row)"
                type="danger"
                size="small"
                effect="dark"
                class="topic-deleted-tag"
              >
                题目已删除
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <!-- 申请学生 -->
        <el-table-column label="申请学生" width="140">
          <template #default="scope">
            <div class="student-cell">
              <span class="stu-id">{{ scope.row.xuehao }}</span>
              <span class="stu-name">{{ scope.row.xueshengxingming }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- 指导教师 -->
        <el-table-column label="指导教师" width="130">
          <template #default="scope">
            <div class="teacher-cell">
              <span class="tea-id">{{ scope.row.jiaoshigonghao }}</span>
              <span class="tea-name">{{ scope.row.jiaoshixingming }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- 申请时间 -->
        <el-table-column label="申请时间" width="150" align="center">
          <template #default="scope">
            <el-tooltip :content="scope.row.shenqingshijianFull" placement="top">
              <span class="time-cell">{{ getRelativeTime(scope.row.shenqingshijian) }}</span>
            </el-tooltip>
          </template>
        </el-table-column>

        <!-- 审核状态 -->
        <el-table-column label="审核状态" width="110" align="center">
          <template #default="scope">
            <span class="status-badge" :class="'status-' + getStatusKey(scope.row.shenhezhuangtai)">
              <span class="status-dot"></span>
              {{ formatApplicationStatus(scope.row.shenhezhuangtai) }}
            </span>
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column label="操作" width="210" align="center" fixed="right">
          <template #default="scope">
            <div class="action-cell" v-if="canOperate">
              <el-tooltip
                v-if="isPending(scope.row.shenhezhuangtai)"
                :content="isTopicDeleted(scope.row) ? '题目已删除，无法通过' : '通过'"
                placement="top"
              >
                <button
                  class="action-btn action-btn--pass"
                  :class="{ 'action-btn--disabled': isTopicDeleted(scope.row) }"
                  :disabled="isTopicDeleted(scope.row)"
                  @click.stop="openPass(scope.row)"
                >
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none">
                    <path d="M5 13l4 4L19 7" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </button>
              </el-tooltip>
              <el-tooltip
                v-if="canReject(scope.row.shenhezhuangtai)"
                :content="isAppliedStatus(scope.row.shenhezhuangtai) ? '撤销通过并驳回' : '驳回'"
                placement="top"
              >
                <button class="action-btn action-btn--reject" @click.stop="openReject(scope.row)">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none">
                    <path d="M6 18L18 6M6 6l12 12" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
                  </svg>
                </button>
              </el-tooltip>
              <el-tooltip content="详情" placement="top">
                <button class="action-btn action-btn--view" @click.stop="openDetail(scope.row)">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <button class="action-btn action-btn--delete" @click.stop="handleDelete(scope.row)">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none">
                    <path d="M3 6h18M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2m3 0v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6h14z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </button>
              </el-tooltip>
            </div>
            <div class="action-cell" v-else>
              <button class="action-btn action-btn--view action-btn--single" @click.stop="openDetail(scope.row)">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
                </svg>
                查看详情
              </button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          :current-page="pagination.page"
          :page-size="pagination.pageSize"
          :page-sizes="[10, 15, 20, 30]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          prev-text="上一页"
          next-text="下一页"
          @current-change="(p) => { pagination.page = p; getList(); }"
          @size-change="(s) => { pagination.pageSize = s; pagination.page = 1; getList(); }"
        />
      </div>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      :title="detailTitle"
      width="680px"
      class="detail-dialog"
      destroy-on-close
    >
      <div class="detail-body" v-if="currentRow">
        <!-- 题目信息 -->
        <div class="detail-section">
          <div class="detail-section-title">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" style="margin-right:6px">
              <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" stroke="#00bcd4" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            题目信息
          </div>
          <el-alert
            v-if="isTopicDeleted(currentRow)"
            title="该题目已删除，请驳回申请或联系学生重新选题"
            type="warning"
            :closable="false"
            show-icon
            class="topic-deleted-alert"
          />
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">题目编号</span>
              <span class="detail-value detail-copy" @click="copyText(currentRow.timubianhao)">
                {{ currentRow.timubianhao }}
                <el-icon><DocumentCopy /></el-icon>
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">课题名称</span>
              <span class="detail-value">
                {{ currentRow.ketimingcheng }}
                <el-tag
                  v-if="isTopicDeleted(currentRow)"
                  type="danger"
                  size="small"
                  effect="dark"
                  class="topic-deleted-tag"
                >
                  题目已删除
                </el-tag>
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">题目类型</span>
              <span class="detail-value">{{ currentRow.timuleixing }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">专业</span>
              <span class="detail-value">{{ currentRow.zhuanye }}</span>
            </div>
            <div class="detail-item detail-item--full">
              <span class="detail-label">指导教师</span>
              <span class="detail-value">{{ currentRow.jiaoshigonghao }} / {{ currentRow.jiaoshixingming }}</span>
            </div>
          </div>
        </div>

        <!-- 申请信息 -->
        <div class="detail-section">
          <div class="detail-section-title">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" style="margin-right:6px">
              <path d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" stroke="#00bcd4" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            申请信息
          </div>
          <div class="detail-grid">
            <div class="detail-item detail-item--full">
              <span class="detail-label">申请学生</span>
              <span class="detail-value">{{ currentRow.xuehao }} / {{ currentRow.xueshengxingming }}</span>
            </div>
            <div class="detail-item detail-item--full">
              <span class="detail-label">申请时间</span>
              <span class="detail-value">{{ currentRow.shenqingshijianFull }}</span>
            </div>
            <div class="detail-item detail-item--full">
              <span class="detail-label">申请原因</span>
              <div class="detail-reason">{{ currentRow.shenqingyuanyin }}</div>
            </div>
          </div>
        </div>

        <!-- 审核记录 -->
        <div class="detail-section">
          <div class="detail-section-title">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" style="margin-right:6px">
              <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" stroke="#00bcd4" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            审核记录
          </div>
          <div class="audit-timeline">
            <div class="timeline-item" v-for="(record, idx) in currentRow.auditRecords" :key="idx">
              <div class="timeline-dot" :class="'timeline-dot--' + record.type"></div>
              <div class="timeline-content">
                <span class="timeline-time">{{ record.time }}</span>
                <span class="timeline-text">{{ record.text }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer v-if="currentRow && canOperate">
        <div class="detail-footer">
          <el-button class="btn-cancel" @click="detailVisible = false">关闭</el-button>
          <el-button
            v-if="isPending(currentRow.shenhezhuangtai)"
            class="btn-pass"
            :disabled="isTopicDeleted(currentRow)"
            @click="openPass(currentRow); detailVisible = false"
          >
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" style="margin-right:4px">
              <path d="M5 13l4 4L19 7" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            通过
          </el-button>
          <el-button v-if="canReject(currentRow.shenhezhuangtai)" class="btn-reject" @click="openReject(currentRow)">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" style="margin-right:4px">
              <path d="M6 18L18 6M6 6l12 12" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
            </svg>
            {{ isAppliedStatus(currentRow.shenhezhuangtai) ? '撤销并驳回' : '驳回' }}
          </el-button>
          <el-button class="btn-delete" @click="handleDelete(currentRow); detailVisible = false">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </div>
      </template>
      <template #footer v-else-if="currentRow">
        <div class="detail-footer">
          <el-button class="btn-cancel" @click="detailVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 通过确认弹窗 -->
    <el-dialog
      v-model="passVisible"
      title="通过申请"
      width="480px"
      class="pass-dialog"
      destroy-on-close
    >
      <div class="pass-body">
        <div class="pass-info" v-if="passRow">
          <p>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" style="margin-right:6px;vertical-align:middle">
              <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" stroke="#4caf50" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            课题：<strong>{{ passRow.ketimingcheng }}</strong>
          </p>
          <p>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" style="margin-right:6px;vertical-align:middle">
              <path d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" stroke="#4caf50" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            学生：{{ passRow.xuehao }} {{ passRow.xueshengxingming }}
          </p>
        </div>
        <el-form :model="passForm" class="pass-form">
          <el-form-item label="通过理由">
            <el-input
              v-model="passForm.reason"
              type="textarea"
              :rows="4"
              maxlength="200"
              show-word-limit
              placeholder="请填写通过理由（选填，不填将使用默认说明）"
            />
          </el-form-item>
        </el-form>
        <div class="pass-footer">
          <el-button class="btn-cancel" @click="passVisible = false">取消</el-button>
          <el-button class="btn-pass btn-pass--confirm" @click="handlePass">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" style="margin-right:4px">
              <path d="M5 13l4 4L19 7" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            确认通过
          </el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 驳回确认弹窗 -->
    <el-dialog
      v-model="rejectVisible"
      title="驳回申请"
      width="480px"
      class="reject-dialog"
      destroy-on-close
    >
      <div class="reject-body">
        <el-alert
          v-if="rejectRow && rejectRow.id !== 'batch' && isAppliedStatus(rejectRow.shenhezhuangtai)"
          title="该申请已通过，驳回后将撤销学生选题资格，题目将重新开放申请"
          type="warning"
          :closable="false"
          show-icon
          class="reject-revoke-alert"
        />
        <div class="reject-info" v-if="rejectRow">
          <p>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" style="margin-right:6px;vertical-align:middle">
              <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" stroke="#ff9800" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            课题：<strong>{{ rejectRow.ketimingcheng }}</strong>
          </p>
          <p>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" style="margin-right:6px;vertical-align:middle">
              <path d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" stroke="#ff9800" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            学生：{{ rejectRow.xuehao }} {{ rejectRow.xueshengxingming }}
          </p>
        </div>
        <el-form :model="rejectForm" class="reject-form">
          <el-form-item label="驳回原因">
            <el-input
              v-model="rejectForm.reason"
              type="textarea"
              :rows="4"
              maxlength="200"
              show-word-limit
              placeholder="请填写驳回原因（选填，不填将使用默认说明）"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="reject-footer">
          <el-button class="btn-cancel" @click="rejectVisible = false">取消</el-button>
          <el-button class="btn-reject btn-reject--confirm" @click="handleReject">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" style="margin-right:4px">
              <path d="M6 18L18 6M6 6l12 12" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
            </svg>
            确认驳回
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Toast 提示 -->
    <transition name="toast-fade">
      <div class="toast-notification" v-if="toast.visible">
        <div class="toast-icon" :class="'toast-' + toast.type">
          <svg v-if="toast.type === 'success'" width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="M5 13l4 4L19 7" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <svg v-else-if="toast.type === 'error'" width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="M6 18L18 6M6 6l12 12" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
          </svg>
          <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
            <path d="M12 8v4M12 16h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <span class="toast-text">{{ toast.message }}</span>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import {
  Search, RefreshRight, Document, Collection, User, Avatar,
  Check, DocumentCopy, Download, Delete
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '@/utils/http'

// ============================================================
// 状态
// ============================================================
const tableData = ref([])
const loading = ref(false)
const tableRef = ref(null)
const total = ref(0)
const selectedRows = ref([])

// 筛选
const filter = ref({
  timubianhao: '', ketimingcheng: '', shenhezhuangtai: '',
  xueshengxingming: '', jiaoshixingming: '',
  zhuanye: '', timuleixing: ''
})

// 分页
const pagination = ref({ page: 1, pageSize: 10 })

// 详情弹窗
const detailVisible = ref(false)
const detailTitle = ref('选题申请详情')
const currentRow = ref(null)

// 驳回弹窗
const rejectVisible = ref(false)
const rejectRow = ref(null)
const rejectForm = ref({ reason: '' })

// 通过弹窗
const passVisible = ref(false)
const passRow = ref(null)
const passForm = ref({ reason: '' })

// Toast
const toast = ref({ visible: false, message: '', type: 'success' })

const STATUS_PENDING = '未审核'
const STATUS_APPROVED = '通过'
const STATUS_REJECTED = '驳回'
const APPLIED_STATUS_VALUES = [STATUS_APPROVED, '已审核', '已通过', '是', '已申请']
const REJECTED_STATUS_VALUES = [STATUS_REJECTED, '否', '已驳回']
const PENDING_STATUS_VALUES = [STATUS_PENDING, '未申请', '待审核']

// 当前登录用户信息
const currentUser = ref(null)
const sessionTable = ref(localStorage.getItem('sessionTable') || '')

// ============================================================
// 获取当前登录用户信息
// ============================================================
const getCurrentUser = () => {
  sessionTable.value = localStorage.getItem('sessionTable') || ''
  const sessionRequest = sessionTable.value === 'jiaoshi'
    ? http.get('/jiaoshi/session')
    : sessionTable.value === 'users'
      ? http.get('/users/session')
      : null

  if (!sessionRequest) {
    currentUser.value = null
    return
  }

  sessionRequest
    .then(res => {
      if (res.data && res.data.code === 0) {
        currentUser.value = res.data.data
      }
    })
    .catch(() => {
      currentUser.value = null
    })
}

// 筛选选项 - 从数据库动态获取
const zhuanyeOptions = ref([])
const timuleixingOptions = ref([])

// ============================================================
// 获取筛选选项（从数据库真实数据）
// ============================================================
const getFilterOptions = () => {
  // 教师身份：专业选项仅显示本教师的专业
  if (currentUser.value && currentUser.value.zhuanye) {
    zhuanyeOptions.value = [currentUser.value.zhuanye]
  } else {
    // 获取专业选项 - 从xuesheng表中获取所有不同的专业
    http.get('/xuesheng/lists')
      .then(res => {
        if (res.data && res.data.code === 0) {
          const list = res.data.data || []
          const zhuanyes = [...new Set(list.map(item => item.zhuanye).filter(Boolean))]
          zhuanyeOptions.value = zhuanyes.sort()
        }
      })
      .catch(err => {
        console.error('获取专业选项失败:', err)
      })
  }

  // 获取题目类型选项 - 从timuleixing表中获取所有类型
  http.get('/timuleixing/lists')
    .then(res => {
      if (res.data && res.data.code === 0) {
        const list = res.data.data || []
        timuleixingOptions.value = list.map(item => item.timuleixing).filter(Boolean).sort()
      }
    })
    .catch(err => {
      console.error('获取题目类型选项失败:', err)
    })
}

// ============================================================
// 从后端API获取真实数据
// ============================================================
const enrichTopicDeletedFlag = async (rows) => {
  if (!rows.length) return rows
  const hasBackendFlag = rows.some(row => row.timuyishanchu === true || row.timuyishanchu === false)
  if (hasBackendFlag) return rows

  const codes = [...new Set(rows.map(row => row.timubianhao).filter(Boolean))]
  if (!codes.length) {
    rows.forEach(row => { row.timuyishanchu = true })
    return rows
  }

  try {
    const res = await http.get('/timuxinxi/lists')
    if (res.data && res.data.code === 0) {
      const existingCodes = new Set(
        (res.data.data || [])
          .map(item => item.timubianhao)
          .filter(Boolean)
      )
      rows.forEach(row => {
        const code = row.timubianhao ? String(row.timubianhao).trim() : ''
        row.timuyishanchu = !code || !existingCodes.has(code)
      })
    }
  } catch (err) {
    console.error('检查题目是否已删除失败:', err)
  }
  return rows
}

const getList = () => {
  loading.value = true
  const params = {
    page: pagination.value.page,
    limit: pagination.value.pageSize
  }

  // 添加筛选条件
  if (filter.value.timubianhao) {
    params.timubianhao = filter.value.timubianhao
  }
  if (filter.value.ketimingcheng) {
    params.ketimingcheng = filter.value.ketimingcheng
  }
  if (filter.value.shenhezhuangtai) {
    params.shenhezhuangtai = filter.value.shenhezhuangtai
  }
  if (filter.value.xueshengxingming) {
    params.xueshengxingming = filter.value.xueshengxingming
  }
  if (filter.value.jiaoshixingming) {
    params.jiaoshixingming = filter.value.jiaoshixingming
  }
  if (filter.value.zhuanye) {
    params.zhuanye = filter.value.zhuanye
  }
  if (filter.value.timuleixing) {
    params.timuleixing = filter.value.timuleixing
  }

  http.get('/xuantishenqing/page', { params })
    .then(async res => {
      loading.value = false
      if (res.data && res.data.code === 0) {
        const data = res.data.data
        let rows = (data.list || []).map(item => ({
          ...item,
          shenhezhuangtai: normalizeApplicationStatus(item.shenhezhuangtai),
          shenqingshijianFull: item.shenqingshijian || '',
          auditRecords: buildAuditRecords(item)
        }))
        rows = await enrichTopicDeletedFlag(rows)
        tableData.value = rows
        total.value = Number(data.total || 0)
      }
    })
    .catch(err => {
      loading.value = false
      console.error('获取数据失败:', err)
    })
}

// 根据审核状态构建审核记录
const buildAuditRecords = (row) => {
  if (row.shenhejilu) {
    try {
      const logs = JSON.parse(row.shenhejilu)
      if (Array.isArray(logs) && logs.length) {
        return logs.map(item => ({
          time: item.time || '',
          text: item.reason ? `${item.text || ''}：${item.reason}` : (item.text || ''),
          type: item.action === 'pass' ? 'pass' : item.action === 'reject' ? 'reject' : 'info'
        }))
      }
    } catch (err) {
      console.warn('解析审核流程记录失败:', err)
    }
  }

  const records = []
  if (row.shenqingshijian) {
    records.push({
      time: row.shenqingshijian,
      text: '学生提交选题申请',
      type: 'info'
    })
  }
  if (isAppliedStatus(row.shenhezhuangtai) && row.shenheshijian) {
    const reason = extractPassReasonFromApplication(row.shenqingyuanyin)
    records.push({
      time: row.shenheshijian,
      text: reason ? `导师确认申请：${reason}` : '导师确认申请',
      type: 'pass'
    })
  } else if (isRejectedStatus(row.shenhezhuangtai) && row.shenheshijian) {
    const reason = extractRejectReasonFromApplication(row.shenqingyuanyin)
    records.push({
      time: row.shenheshijian,
      text: reason ? `导师驳回申请：${reason}` : '导师驳回申请',
      type: 'reject'
    })
  }
  return records
}

const extractRejectReasonFromApplication = (reasonText) => {
  if (!reasonText) return ''
  const marker = '[驳回原因]:'
  const index = reasonText.lastIndexOf(marker)
  if (index < 0) return ''
  return reasonText.substring(index + marker.length).trim()
}

const extractPassReasonFromApplication = (reasonText) => {
  if (!reasonText) return ''
  const marker = '[通过原因]:'
  const index = reasonText.lastIndexOf(marker)
  if (index < 0) return ''
  return reasonText.substring(index + marker.length).trim()
}

const stripAuditReasonMarkers = (reasonText) => {
  if (!reasonText) return ''
  return reasonText
    .replace(/\n?\[驳回原因\]:[^\n]*/g, '')
    .replace(/\n?\[通过原因\]:[^\n]*/g, '')
    .trim()
}

const isRejectedStatus = (status) => {
  if (REJECTED_STATUS_VALUES.includes(status)) return true
  return normalizeApplicationStatus(status) === STATUS_REJECTED
}

const normalizeApplicationStatus = (status) => {
  if (!status) return STATUS_PENDING
  if (APPLIED_STATUS_VALUES.includes(status)) return STATUS_APPROVED
  if (REJECTED_STATUS_VALUES.includes(status)) return STATUS_REJECTED
  if (PENDING_STATUS_VALUES.includes(status)) return STATUS_PENDING
  return STATUS_PENDING
}

// ============================================================
// 计算属性
// ============================================================
const filteredData = computed(() => {
  // 数据已在getList中过滤，这里直接返回
  return tableData.value
})

const isTeacherRole = computed(() => sessionTable.value === 'jiaoshi')

const canOperate = computed(() => isTeacherRole.value)

const ensureCanOperate = () => {
  if (canOperate.value) return true
  showToast('仅教师可以操作选题申请', 'error')
  return false
}

// ============================================================
// 方法
// ============================================================
const isAppliedStatus = (status) => normalizeApplicationStatus(status) === STATUS_APPROVED

const formatApplicationStatus = (status) => normalizeApplicationStatus(status)

const isPending = (status) => normalizeApplicationStatus(status) === STATUS_PENDING

const canReject = (status) => !isRejectedStatus(status)

const getStatusKey = (status) => {
  if (isRejectedStatus(status)) return 'reject'
  return isAppliedStatus(status) ? 'pass' : 'pending'
}

const getRelativeTime = (dateStr) => {
  if (!dateStr) return '-'
  const now = new Date()
  const date = new Date(dateStr.replace(' ', 'T'))
  const diff = Math.floor((now - date) / 1000)

  if (diff < 60) return '刚刚'
  if (diff < 3600) return `${Math.floor(diff / 60)}分钟前`
  if (diff < 86400) return `${Math.floor(diff / 3600)}小时前`
  if (diff < 2592000) return `${Math.floor(diff / 86400)}天前`
  return dateStr.split(' ')[0]
}

const headerCellStyle = () => ({
  background: '#0d2137',
  color: '#90a4ae',
  fontWeight: '600',
  fontSize: '13px',
  padding: '12px 8px',
  borderBottom: '1px solid rgba(34, 211, 238, 0.14)',
  textAlign: 'center'
})

const rowClassName = ({ row }) => {
  return selectedRows.value.some(r => r.id === row.id) ? 'table-row--selected' : ''
}

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

const handleRowClick = (row) => {
  nextTick(() => {
    if (!tableRef.value) return
    const isSelected = selectedRows.value.some(r => r.id === row.id)
    if (isSelected) {
      tableRef.value.toggleRowSelection(row, false)
    } else {
      tableRef.value.toggleRowSelection(row, true)
    }
  })
}

const clearSelection = () => {
  tableRef.value?.clearSelection()
}

const handleFilter = () => {
  pagination.value.page = 1
  getList()
}

const handleReset = () => {
  filter.value = {
    timubianhao: '', ketimingcheng: '', shenhezhuangtai: '',
    xueshengxingming: '', jiaoshixingming: '',
    zhuanye: '', timuleixing: ''
  }
  pagination.value.page = 1
  getList()
}

const showToast = (message, type = 'success') => {
  toast.value = { visible: true, message, type }
  setTimeout(() => { toast.value.visible = false }, 2500)
}

const isTopicDeleted = (row) => {
  return !!(row && (row.timuyishanchu === true || row.timuyishanchu === 'true'))
}

// 通过操作
const openPass = (row) => {
  if (!ensureCanOperate()) return
  if (isTopicDeleted(row)) {
    showToast('该题目已删除，无法通过申请', 'error')
    return
  }
  passRow.value = row
  passForm.value.reason = ''
  detailVisible.value = false
  passVisible.value = true
}

const openBatchPass = () => {
  if (!ensureCanOperate()) return
  if (!selectedRows.value.length) return
  const passableRows = selectedRows.value.filter(row => isPending(row.shenhezhuangtai) && !isTopicDeleted(row))
  const skippedCount = selectedRows.value.filter(row => isPending(row.shenhezhuangtai) && isTopicDeleted(row)).length
  if (!passableRows.length) {
    showToast(skippedCount ? '所选申请关联的题目已删除，无法通过' : '没有可通过的申请', 'error')
    return
  }
  passRow.value = {
    ketimingcheng: `已选中 ${passableRows.length} 项可通过申请`,
    xuehao: '',
    xueshengxingming: '',
    id: 'batch'
  }
  passForm.value.reason = ''
  passVisible.value = true
}

const buildPassReason = (row, reason) => {
  const originalReason = stripAuditReasonMarkers(row.shenqingyuanyin || '')
  const base = originalReason ? `${originalReason}\n` : ''
  return `${base}[通过原因]: ${reason}`
}

const handlePass = () => {
  if (!ensureCanOperate()) return
  const reasonText = (passForm.value.reason || '').trim()
  if (passRow.value.id === 'batch') {
    const passableRows = selectedRows.value.filter(row => isPending(row.shenhezhuangtai) && !isTopicDeleted(row))
    if (!passableRows.length) {
      showToast('没有可通过的申请', 'error')
      return
    }
    const promises = passableRows.map(row => {
      const payload = {
        id: row.id,
        shenhezhuangtai: STATUS_APPROVED
      }
      if (reasonText) {
        payload.shenqingyuanyin = buildPassReason(row, reasonText)
      }
      return http.put(`/xuantishenqing/update`, payload)
    })
    Promise.all(promises).then(() => {
      showToast(`已确认 ${passableRows.length} 项申请`, 'success')
      clearSelection()
      passVisible.value = false
      getList()
    }).catch(() => {
      showToast('批量通过失败', 'error')
    })
    return
  }

  const payload = {
    id: passRow.value.id,
    shenhezhuangtai: STATUS_APPROVED
  }
  if (reasonText) {
    payload.shenqingyuanyin = buildPassReason(passRow.value, reasonText)
  }
  http.put(`/xuantishenqing/update`, payload).then(res => {
    if (res.data && res.data.code === 0) {
      showToast(`「${passRow.value.ketimingcheng}」已确认申请`, 'success')
      passVisible.value = false
      getList()
    }
  }).catch(() => {
    showToast('操作失败', 'error')
  })
}

// 驳回操作
const openReject = (row) => {
  if (!ensureCanOperate()) return
  rejectRow.value = row
  rejectForm.value.reason = ''
  detailVisible.value = false
  rejectVisible.value = true
}

const openBatchReject = () => {
  if (!ensureCanOperate()) return
  if (!selectedRows.value.length) return
  const rejectableRows = selectedRows.value.filter(row => canReject(row.shenhezhuangtai))
  if (!rejectableRows.length) {
    showToast('所选申请均已驳回，无需重复操作', 'error')
    return
  }
  rejectRow.value = {
    ketimingcheng: `已选中 ${rejectableRows.length} 项可驳回申请`,
    xuehao: '',
    xueshengxingming: '',
    id: 'batch'
  }
  rejectForm.value.reason = ''
  rejectVisible.value = true
}

const buildRejectReason = (row, reason) => {
  const originalReason = stripAuditReasonMarkers(row.shenqingyuanyin || '')
  const base = originalReason ? `${originalReason}\n` : ''
  return `${base}[驳回原因]: ${reason}`
}

const getRejectReasonText = () => {
  const customReason = (rejectForm.value.reason || '').trim()
  if (customReason) return customReason
  if (rejectRow.value && rejectRow.value.id !== 'batch' && isAppliedStatus(rejectRow.value.shenhezhuangtai)) {
    return '导师撤销已通过申请'
  }
  return '导师驳回申请'
}

const handleReject = () => {
  if (!ensureCanOperate()) return
  const reasonText = getRejectReasonText()
  if (rejectRow.value.id === 'batch') {
    const rejectableRows = selectedRows.value.filter(row => canReject(row.shenhezhuangtai))
    if (!rejectableRows.length) {
      showToast('没有可驳回的申请', 'error')
      return
    }
    const promises = rejectableRows.map(row =>
      http.put(`/xuantishenqing/update`, {
        id: row.id,
        shenhezhuangtai: STATUS_REJECTED,
        shenqingyuanyin: buildRejectReason(row, reasonText)
      })
    )
    Promise.all(promises).then(() => {
      showToast(`已驳回 ${rejectableRows.length} 项申请`, 'error')
      clearSelection()
      rejectVisible.value = false
      getList()
    }).catch(() => {
      showToast('批量驳回失败', 'error')
    })
  } else {
    // 单条驳回
    http.put(`/xuantishenqing/update`, {
      id: rejectRow.value.id,
      shenhezhuangtai: STATUS_REJECTED,
      shenqingyuanyin: buildRejectReason(rejectRow.value, reasonText)
    }).then(res => {
      if (res.data && res.data.code === 0) {
        showToast(`「${rejectRow.value.ketimingcheng}」已驳回`, 'error')
        rejectVisible.value = false
        getList()
      }
    }).catch(() => {
      showToast('驳回失败', 'error')
    })
  }
}

// 批量通过
const handleBatchPass = () => {
  openBatchPass()
}

// 详情
const openDetail = (row) => {
  currentRow.value = {
    ...row,
    auditRecords: buildAuditRecords(row)
  }
  detailTitle.value = '选题申请详情'
  detailVisible.value = true
}

// 导出
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 删除操作
const handleDelete = (row) => {
  if (!ensureCanOperate()) return
  const rows = row ? [row] : selectedRows.value
  if (!rows.length) {
    showToast('请选择要删除的申请', 'error')
    return
  }

  const label = row
    ? `「${row.ketimingcheng || row.timubianhao || '该申请'}」`
    : `选中的 ${rows.length} 项申请`

  ElMessageBox.confirm(`确定删除${label}吗？删除后不可恢复。`, '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    http.post('/xuantishenqing/delete', rows.map(item => item.id))
      .then(res => {
        if (res.data && res.data.code === 0) {
          showToast(row ? '删除成功' : `已删除 ${rows.length} 项申请`, 'success')
          if (currentRow.value && rows.some(item => item.id === currentRow.value.id)) {
            detailVisible.value = false
            currentRow.value = null
          }
          clearSelection()
          getList()
        } else {
          showToast(res.data?.msg || '删除失败', 'error')
        }
      })
      .catch(() => {
        showToast('删除失败', 'error')
      })
  }).catch(() => {})
}

// 复制文本
const copyText = (text) => {
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

onMounted(() => {
  getCurrentUser()
  // 等用户信息加载后再获取筛选选项和列表数据
  setTimeout(() => {
    getFilterOptions()
    getList()
  }, 100)
})
</script>

<style lang="scss" scoped>
/* ============================================================
   页面基础
   ============================================================ */
.xuantishenqing-page {
  padding: 16px 20px;
  background: transparent;
  min-height: 100%;
  box-sizing: border-box;
}

/* ============================================================
   筛选卡片
   ============================================================ */
.filter-card {
  background: #132f4c;
  border-radius: 10px;
  border: 1px solid rgba(34, 211, 238, 0.14);
  padding: 16px 20px;
  margin-bottom: 16px;
}

.filter-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  flex: 1;
}

.filter-input {
  width: 160px;
  :deep(.el-input__wrapper) {
    background: rgba(0, 188, 212, 0.05) !important;
    border: 1px solid rgba(34, 211, 238, 0.18) !important;
    box-shadow: none !important;
    border-radius: 8px;
    padding: 0 12px;
    &:hover, &:focus {
      border-color: #00bcd4 !important;
    }
  }
  :deep(.el-input__inner) {
    color: #e0e0e0;
    &::placeholder { color: rgba(224, 224, 224, 0.4); }
  }
  :deep(.el-input__prefix) { color: #00bcd4; }
}

.filter-input-wide { width: 200px; }

.filter-actions {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-shrink: 0;
}

.search-btn {
  border: none;
  border-radius: 8px;
  padding: 0 18px;
  height: 36px;
  background: linear-gradient(135deg, #00bcd4 0%, #00838f 100%);
  color: #fff;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  &:hover {
    opacity: 0.9;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 188, 212, 0.4);
  }
}

.reset-btn {
  border: 1px solid rgba(34, 211, 238, 0.2);
  border-radius: 8px;
  padding: 0 16px;
  height: 36px;
  background: transparent;
  color: rgba(224, 224, 224, 0.7);
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  &:hover {
    border-color: #00bcd4;
    color: #00bcd4;
  }
}

/* ============================================================
   操作栏
   ============================================================ */
.action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid rgba(34, 211, 238, 0.1);
  flex-wrap: wrap;
  gap: 10px;
}

.action-left { display: flex; gap: 8px; flex-wrap: wrap; align-items: center; }

.readonly-tip {
  font-size: 13px;
  color: rgba(255, 183, 77, 0.95);
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid rgba(255, 183, 77, 0.35);
  background: rgba(255, 183, 77, 0.1);
}

.btn-pass-all {
  border-radius: 8px;
  border: 1px solid rgba(76, 175, 80, 0.35);
  background: rgba(76, 175, 80, 0.12);
  color: #81c784;
  height: 34px;
  padding: 0 16px;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
  &:hover:not(:disabled) {
    background: rgba(76, 175, 80, 0.25);
    border-color: #4caf50;
    color: #a5d6a7;
  }
  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
}

.btn-reject-all {
  border-radius: 8px;
  border: 1px solid rgba(244, 67, 54, 0.35);
  background: rgba(244, 67, 54, 0.12);
  color: #e57373;
  height: 34px;
  padding: 0 16px;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
  &:hover:not(:disabled) {
    background: rgba(244, 67, 54, 0.25);
    border-color: #f44336;
    color: #ef9a9a;
  }
  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
}

.btn-delete-all {
  border-radius: 8px;
  border: 1px solid rgba(255, 87, 34, 0.35);
  background: rgba(255, 87, 34, 0.12);
  color: #ff8a65;
  height: 34px;
  padding: 0 16px;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
  &:hover:not(:disabled) {
    background: rgba(255, 87, 34, 0.22);
    border-color: #ff5722;
    color: #ffab91;
  }
  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
}

.btn-export {
  border-radius: 8px;
  border: 1px solid rgba(34, 211, 238, 0.2);
  background: transparent;
  color: rgba(224, 224, 224, 0.7);
  height: 34px;
  padding: 0 16px;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
  &:hover {
    border-color: #00bcd4;
    color: #00bcd4;
  }
}

.action-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.selected-tip {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #00bcd4;
  strong { font-weight: 600; }
  .el-icon { font-size: 14px; }
}

/* ============================================================
   表格卡片
   ============================================================ */
.table-card {
  background: #132f4c;
  border-radius: 10px;
  border: 1px solid rgba(34, 211, 238, 0.14);
  padding: 16px;
}

/* ============================================================
   表格
   ============================================================ */
.shenqing-table {
  background: transparent !important;
  border: none !important;
  border-radius: 8px;
  overflow: hidden;

  :deep(.el-table__header-wrapper) {
    thead {
      tr {
        background: linear-gradient(135deg, rgba(34, 211, 238, 0.9) 0%, rgba(8, 145, 178, 0.95) 100%);

        th {
          padding: 12px 8px !important;
          background: transparent !important;
          border: none !important;
          border-bottom: none !important;
          text-align: center;

          .cell {
            padding: 0 8px;
            font-weight: 600;
            font-size: 13px;
            color: #fff !important;
          }
        }
      }
    }
  }

  :deep(.el-table__body-wrapper) {
    tr {
      background: #132f4c !important;
      transition: background 0.2s ease;
    }
    tr:nth-child(even) {
      background: #142a42 !important;
    }
    tr:hover > td {
      background: #1a3a5c !important;
      color: #e0e0e0 !important;
    }
    td.el-table__cell {
      background: transparent !important;
      color: #e0e0e0;
      border-bottom: 1px solid rgba(34, 211, 238, 0.08);
      padding: 10px 8px;
      font-size: 13px;
    }
  }

  :deep(.table-row--selected) {
    td.el-table__cell {
      background: #1a3a5c !important;
      border-left: 3px solid #00bcd4;
    }
  }

  :deep(.el-table__column-filter-trigger) { color: #90a4ae; }

  // 复选框
  :deep(.el-checkbox__inner) {
    background: rgba(0, 188, 212, 0.1);
    border-color: rgba(34, 211, 238, 0.3);
  }
  :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
    background: #00bcd4;
    border-color: #00bcd4;
  }
  :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
    color: #00bcd4;
  }
}

/* 表格单元格内容 */
.row-index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  background: rgba(0, 188, 212, 0.1);
  border-radius: 6px;
  font-size: 11px;
  color: #00bcd4;
  font-weight: 600;
}

.topic-name-cell {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  &:hover .topic-name-text { color: #00bcd4; }
}

.topic-deleted-tag {
  flex-shrink: 0;
}

.topic-deleted-alert {
  margin-bottom: 12px;
}

.topic-name-text {
  color: #e0e0e0;
  transition: color 0.2s;
  font-weight: 500;
}

.student-cell, .teacher-cell {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 1px;
}

.stu-id, .tea-id {
  font-size: 11px;
  color: #90a4ae;
}

.stu-name, .tea-name {
  font-size: 13px;
  color: #e0e0e0;
  font-weight: 500;
}

.time-cell {
  font-size: 12px;
  color: #90a4ae;
}

/* 状态徽章 */
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid transparent;

  .status-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    flex-shrink: 0;
  }

  &.status-pending {
    background: rgba(255, 152, 0, 0.15);
    border-color: rgba(255, 152, 0, 0.3);
    color: #ffb74d;
    .status-dot { background: #ff9800; box-shadow: 0 0 4px rgba(255, 152, 0, 0.6); }
  }

  &.status-reviewing {
    background: rgba(33, 150, 243, 0.15);
    border-color: rgba(33, 150, 243, 0.3);
    color: #64b5f6;
    .status-dot { background: #2196f3; box-shadow: 0 0 4px rgba(33, 150, 243, 0.6); }
  }

  &.status-pass {
    background: rgba(76, 175, 80, 0.15);
    border-color: rgba(76, 175, 80, 0.3);
    color: #81c784;
    .status-dot { background: #4caf50; box-shadow: 0 0 4px rgba(76, 175, 80, 0.6); }
  }

  &.status-reject {
    background: rgba(244, 67, 54, 0.15);
    border-color: rgba(244, 67, 54, 0.3);
    color: #e57373;
    .status-dot { background: #f44336; box-shadow: 0 0 4px rgba(244, 67, 54, 0.6); }
  }
}

/* 操作按钮组 */
.action-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border-radius: 6px;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.2s ease;
  background: transparent;

  &:hover { transform: scale(1.1); }
  &:active { transform: scale(0.95); }

  &--pass {
    color: #4caf50;
    border-color: rgba(76, 175, 80, 0.3);
    background: rgba(76, 175, 80, 0.1);
    &:hover { background: rgba(76, 175, 80, 0.25); border-color: #4caf50; }
  }

  &--reject {
    color: #f44336;
    border-color: rgba(244, 67, 54, 0.3);
    background: rgba(244, 67, 54, 0.1);
    &:hover { background: rgba(244, 67, 54, 0.25); border-color: #f44336; }
  }

  &--delete {
    color: #ff7043;
    border-color: rgba(255, 112, 67, 0.3);
    background: rgba(255, 112, 67, 0.1);
    &:hover { background: rgba(255, 112, 67, 0.25); border-color: #ff7043; }
  }

  &--view {
    color: #90a4ae;
    border-color: rgba(144, 164, 174, 0.3);
    background: rgba(144, 164, 174, 0.08);
    &:hover { background: rgba(0, 188, 212, 0.15); border-color: #00bcd4; color: #00bcd4; }
  }

  &--single {
    width: auto;
    padding: 0 10px;
    gap: 5px;
    font-size: 12px;
    color: #90a4ae;
    &:hover { color: #00bcd4; }
  }

  &--disabled,
  &:disabled {
    opacity: 0.45;
    cursor: not-allowed;
    transform: none !important;
    &:hover { transform: none; }
  }
}

/* ============================================================
   分页
   ============================================================ */
.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

:deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-text-color: rgba(224, 224, 224, 0.7);
  --el-pagination-button-bg-color: rgba(0, 188, 212, 0.08);

  .el-pagination__total { color: rgba(224, 224, 224, 0.6); }

  .btn-prev, .btn-next {
    border: 1px solid rgba(34, 211, 238, 0.18);
    border-radius: 8px;
    background: rgba(0, 188, 212, 0.06);
    color: #90a4ae;
    height: 32px;
    line-height: 30px;
    padding: 0 10px;
    &:hover:not(.disabled) {
      color: #fff;
      background: #00bcd4;
      border-color: #00bcd4;
    }
    &.is-disabled {
      background: rgba(255, 255, 255, 0.03);
      border-color: rgba(255, 255, 255, 0.1);
      color: rgba(224, 224, 224, 0.3);
      cursor: not-allowed;
    }
  }

  .el-pager {
    li {
      border: 1px solid rgba(34, 211, 238, 0.18);
      border-radius: 8px;
      background: rgba(0, 188, 212, 0.06);
      color: #90a4ae;
      margin: 0 3px;
      min-width: 32px;
      height: 32px;
      line-height: 30px;
      font-size: 13px;
      &:hover:not(.is-active) {
        color: #fff;
        background: rgba(0, 188, 212, 0.2);
        border-color: #00bcd4;
      }
      &.is-active {
        background: linear-gradient(135deg, #00bcd4 0%, #00838f 100%);
        border-color: #00bcd4;
        color: #fff;
      }
    }
  }

  .el-pagination__sizes {
    .el-select .el-input__wrapper {
      background: rgba(0, 188, 212, 0.06);
      border-color: rgba(34, 211, 238, 0.18);
      box-shadow: none;
      border-radius: 8px;
    }
    .el-select .el-input__inner { color: #90a4ae; }
  }

  .el-pagination__jump {
    color: rgba(224, 224, 224, 0.6);
    .el-input__wrapper {
      background: rgba(0, 188, 212, 0.06);
      border-color: rgba(34, 211, 238, 0.18);
      box-shadow: none;
      border-radius: 8px;
    }
    .el-input__inner { color: #90a4ae; }
  }
}

/* ============================================================
   详情弹窗
   ============================================================ */
.detail-dialog {
  :deep(.el-dialog) {
    background: #132f4c !important;
    border: 1px solid rgba(34, 211, 238, 0.2) !important;
    border-radius: 16px !important;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4) !important;
  }
  :deep(.el-dialog__header) {
    border-bottom: 1px solid rgba(34, 211, 238, 0.12);
    padding: 16px 24px;
    .el-dialog__title {
      color: #00bcd4 !important;
      font-weight: 600;
      font-size: 15px;
    }
  }
  :deep(.el-dialog__body) { padding: 20px 24px; }
  :deep(.el-dialog__footer) { border-top: 1px solid rgba(34, 211, 238, 0.1); padding: 14px 24px; }
  :deep(.el-icon.el-dialog__close) { color: #90a4ae; &:hover { color: #00bcd4; } }
}

.detail-body { display: flex; flex-direction: column; gap: 20px; }

.detail-section-title {
  display: flex;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #e0e0e0;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(34, 211, 238, 0.1);
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 20px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  &--full { grid-column: 1 / -1; }
}

.detail-label {
  font-size: 11px;
  color: #90a4ae;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-value {
  font-size: 13px;
  color: #e0e0e0;
  line-height: 1.5;
  display: flex;
  align-items: center;
  gap: 6px;
}

.detail-copy {
  cursor: pointer;
  &:hover { color: #00bcd4; }
  .el-icon { font-size: 12px; color: #90a4ae; }
  &:hover .el-icon { color: #00bcd4; }
}

.detail-reason {
  font-size: 13px;
  color: #e0e0e0;
  line-height: 1.6;
  background: rgba(0, 188, 212, 0.05);
  border: 1px solid rgba(34, 211, 238, 0.1);
  border-radius: 8px;
  padding: 10px 12px;
}

/* 审核时间线 */
.audit-timeline {
  display: flex;
  flex-direction: column;
  gap: 0;
  position: relative;
  padding-left: 16px;

  &::before {
    content: '';
    position: absolute;
    left: 5px;
    top: 8px;
    bottom: 8px;
    width: 1px;
    background: rgba(34, 211, 238, 0.15);
  }
}

.timeline-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  position: relative;
  padding: 6px 0;
}

.timeline-dot {
  width: 11px;
  height: 11px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 3px;
  border: 2px solid;
  background: #132f4c;

  &--info { border-color: #90a4ae; }
  &--pass { border-color: #4caf50; }
  &--reject { border-color: #f44336; }
  &--pending { border-color: #2196f3; }
}

.timeline-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.timeline-time {
  font-size: 11px;
  color: #90a4ae;
}

.timeline-text {
  font-size: 13px;
  color: #e0e0e0;
}

/* 详情底部按钮 */
.detail-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
}

.btn-cancel {
  border: 1px solid rgba(34, 211, 238, 0.2);
  border-radius: 8px;
  background: transparent;
  color: #90a4ae;
  height: 36px;
  padding: 0 20px;
  font-size: 14px;
  &:hover { border-color: #00bcd4; color: #00bcd4; }
}

.btn-pass {
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #4caf50 0%, #388e3c 100%);
  color: #fff;
  height: 36px;
  padding: 0 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
  &:hover { opacity: 0.9; box-shadow: 0 4px 12px rgba(76, 175, 80, 0.4); }
}

.btn-reject {
  border: 1px solid rgba(244, 67, 54, 0.35);
  border-radius: 8px;
  background: rgba(244, 67, 54, 0.12);
  color: #e57373;
  height: 36px;
  padding: 0 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
  &:hover:not(:disabled) {
    background: rgba(244, 67, 54, 0.25);
    border-color: #f44336;
    color: #ef9a9a;
  }
}

.btn-delete {
  border: 1px solid rgba(255, 87, 34, 0.35);
  border-radius: 8px;
  background: rgba(255, 87, 34, 0.12);
  color: #ff8a65;
  height: 36px;
  padding: 0 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
  &:hover {
    background: rgba(255, 87, 34, 0.22);
    border-color: #ff5722;
    color: #ffab91;
  }
}

/* ============================================================
   驳回弹窗
   ============================================================ */
.pass-dialog {
  :deep(.el-dialog) {
    background: #132f4c !important;
    border: 1px solid rgba(34, 211, 238, 0.2) !important;
    border-radius: 16px !important;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4) !important;
  }
  :deep(.el-dialog__header) {
    border-bottom: 1px solid rgba(34, 211, 238, 0.12);
    padding: 16px 24px;
    .el-dialog__title { color: #4caf50 !important; font-weight: 600; font-size: 15px; }
  }
  :deep(.el-dialog__body) { padding: 20px 24px; }
  :deep(.el-icon.el-dialog__close) { color: #90a4ae; &:hover { color: #4caf50; } }
}

.pass-info {
  background: rgba(76, 175, 80, 0.08);
  border: 1px solid rgba(76, 175, 80, 0.2);
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 16px;
  p {
    margin: 0 0 6px 0;
    font-size: 13px;
    color: #e0e0e0;
    display: flex;
    align-items: center;
    &:last-child { margin-bottom: 0; }
    strong { color: #81c784; }
  }
}

.pass-form {
  :deep(.el-form-item__label) { color: #90a4ae; font-size: 13px; }
  :deep(.el-textarea__inner) {
    background: rgba(0, 188, 212, 0.05) !important;
    border: 1px solid rgba(34, 211, 238, 0.18) !important;
    border-radius: 8px !important;
    box-shadow: none !important;
    color: #e0e0e0 !important;
    padding: 12px;
    font-size: 13px;
    &:hover, &:focus { border-color: #00bcd4 !important; }
  }
  :deep(.el-textarea__count) { background: transparent; color: #90a4ae; font-size: 11px; }
}

.pass-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;

  .btn-pass--confirm {
    background: linear-gradient(135deg, #43a047, #2e7d32);
    border: 1px solid rgba(76, 175, 80, 0.6);
    color: #fff;
    cursor: pointer;

    &:hover {
      background: linear-gradient(135deg, #66bb6a, #388e3c);
      border-color: #4caf50;
      color: #fff;
      opacity: 1;
      box-shadow: 0 4px 14px rgba(76, 175, 80, 0.35);
    }
  }
}

.reject-dialog {
  :deep(.el-dialog) {
    background: #132f4c !important;
    border: 1px solid rgba(34, 211, 238, 0.2) !important;
    border-radius: 16px !important;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4) !important;
  }
  :deep(.el-dialog__header) {
    border-bottom: 1px solid rgba(34, 211, 238, 0.12);
    padding: 16px 24px;
    .el-dialog__title { color: #ff9800 !important; font-weight: 600; font-size: 15px; }
  }
  :deep(.el-dialog__body) { padding: 20px 24px; }
  :deep(.el-dialog__footer) { border-top: 1px solid rgba(34, 211, 238, 0.1); padding: 14px 24px; }
  :deep(.el-icon.el-dialog__close) { color: #90a4ae; &:hover { color: #ff9800; } }
}

.reject-revoke-alert {
  margin-bottom: 16px;
}

.reject-info {
  background: rgba(255, 152, 0, 0.08);
  border: 1px solid rgba(255, 152, 0, 0.2);
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 16px;
  p {
    margin: 0 0 6px 0;
    font-size: 13px;
    color: #e0e0e0;
    display: flex;
    align-items: center;
    &:last-child { margin-bottom: 0; }
    strong { color: #ffb74d; }
  }
}

.reject-form {
  :deep(.el-form-item__label) { color: #90a4ae; font-size: 13px; }
  :deep(.el-textarea__inner) {
    background: rgba(0, 188, 212, 0.05) !important;
    border: 1px solid rgba(34, 211, 238, 0.18) !important;
    border-radius: 8px !important;
    box-shadow: none !important;
    color: #e0e0e0 !important;
    padding: 12px;
    font-size: 13px;
    &:hover, &:focus { border-color: #00bcd4 !important; }
  }
  :deep(.el-textarea__count) { background: transparent; color: #90a4ae; font-size: 11px; }
}

.reject-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;

  .btn-reject--confirm {
    background: linear-gradient(135deg, #e53935, #c62828);
    border: 1px solid rgba(244, 67, 54, 0.6);
    color: #fff;
    cursor: pointer;

    &:hover {
      background: linear-gradient(135deg, #ef5350, #d32f2f);
      border-color: #f44336;
      color: #fff;
      opacity: 1;
      box-shadow: 0 4px 14px rgba(244, 67, 54, 0.35);
    }
  }
}

/* ============================================================
   Toast 通知
   ============================================================ */
.toast-notification {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 20px;
  border-radius: 10px;
  z-index: 9999;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);

  &.toast-success {
    background: rgba(30, 58, 95, 0.95);
    border: 1px solid rgba(76, 175, 80, 0.4);
    color: #81c784;
  }
  &.toast-error {
    background: rgba(30, 58, 95, 0.95);
    border: 1px solid rgba(244, 67, 54, 0.4);
    color: #e57373;
  }
  &.toast-info {
    background: rgba(30, 58, 95, 0.95);
    border: 1px solid rgba(33, 150, 243, 0.4);
    color: #64b5f6;
  }
}

.toast-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.toast-text { font-size: 13px; font-weight: 500; }

.toast-fade-enter-active,
.toast-fade-leave-active {
  transition: all 0.3s ease;
}
.toast-fade-enter-from,
.toast-fade-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(-10px);
}

/* ============================================================
   响应式
   ============================================================ */
@media (max-width: 1024px) {
  .filter-group { gap: 8px; }
  .filter-input { width: 140px; }
  .filter-input-wide { width: 170px; }
}

@media (max-width: 768px) {
  .filter-row { flex-direction: column; }
  .filter-group { width: 100%; }
  .filter-actions { width: 100%; }
  .detail-grid { grid-template-columns: 1fr; }
  .action-row { flex-direction: column; align-items: flex-start; }
}
</style>
