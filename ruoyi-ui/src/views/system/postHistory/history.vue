<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="岗位ID" prop="postId">
        <el-input
          v-model="queryParams.postId"
          placeholder="请输入岗位ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="岗位名称" prop="postName">
        <el-input
          v-model="queryParams.postName"
          placeholder="请输入岗位名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="变更类型" prop="changeType">
        <el-select v-model="queryParams.changeType" placeholder="请选择变更类型" clearable>
          <el-option label="新增" value="1" />
          <el-option label="修改" value="2" />
          <el-option label="删除" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作时间">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-sort"
          size="mini"
          :disabled="compareDisabled"
          @click="handleCompare"
          v-hasPermi="['system:postHistory:query']"
        >版本对比</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="historyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="版本号" align="center" prop="version" width="80" />
      <el-table-column label="岗位ID" align="center" prop="postId" width="80" />
      <el-table-column label="岗位编码" align="center" prop="postCode" />
      <el-table-column label="岗位名称" align="center" prop="postName" />
      <el-table-column label="岗位排序" align="center" prop="postSort" width="80" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="变更类型" align="center" prop="changeType">
        <template slot-scope="scope">
          <el-tag :type="getChangeTypeTag(scope.row.changeType)">
            {{ getChangeTypeLabel(scope.row.changeType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作人" align="center" prop="operBy" />
      <el-table-column label="操作时间" align="center" prop="operTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['system:postHistory:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="handleRollback(scope.row)"
            v-hasPermi="['system:postHistory:rollback']"
          >回滚</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog title="变更详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border v-if="currentHistory">
        <el-descriptions-item label="岗位编码">{{ currentHistory.postCode }}</el-descriptions-item>
        <el-descriptions-item label="岗位名称">{{ currentHistory.postName }}</el-descriptions-item>
        <el-descriptions-item label="岗位排序">{{ currentHistory.postSort }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentHistory.status === '0' ? '正常' : '停用' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentHistory.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentHistory.operBy }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ currentHistory.operTime }}</el-descriptions-item>
        <el-descriptions-item label="操作IP" :span="2">{{ currentHistory.operIp || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">变更内容</el-divider>
      <el-table :data="changeDetails" border v-if="changeDetails.length > 0">
        <el-table-column label="字段" align="center" prop="fieldLabel" />
        <el-table-column label="变更前" align="center" prop="oldValue">
          <template slot-scope="scope">
            <span :class="{ 'text-danger': scope.row.oldValue !== scope.row.newValue }">{{ scope.row.oldValue || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="变更后" align="center" prop="newValue">
          <template slot-scope="scope">
            <span :class="{ 'text-success': scope.row.oldValue !== scope.row.newValue }">{{ scope.row.newValue || '-' }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-empty description="无变更内容" v-else />
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="版本对比" :visible.sync="compareOpen" width="900px" append-to-body>
      <el-row :gutter="20">
        <el-col :span="12">
          <h4 style="text-align: center;">版本 {{ compareData.history1 ? compareData.history1.version : '' }}</h4>
          <el-descriptions :column="1" border size="small" v-if="compareData.history1">
            <el-descriptions-item label="岗位编码">{{ compareData.history1.postCode }}</el-descriptions-item>
            <el-descriptions-item label="岗位名称">{{ compareData.history1.postName }}</el-descriptions-item>
            <el-descriptions-item label="岗位排序">{{ compareData.history1.postSort }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ compareData.history1.status === '0' ? '正常' : '停用' }}</el-descriptions-item>
            <el-descriptions-item label="操作时间">{{ compareData.history1.operTime }}</el-descriptions-item>
          </el-descriptions>
        </el-col>
        <el-col :span="12">
          <h4 style="text-align: center;">版本 {{ compareData.history2 ? compareData.history2.version : '' }}</h4>
          <el-descriptions :column="1" border size="small" v-if="compareData.history2">
            <el-descriptions-item label="岗位编码">{{ compareData.history2.postCode }}</el-descriptions-item>
            <el-descriptions-item label="岗位名称">{{ compareData.history2.postName }}</el-descriptions-item>
            <el-descriptions-item label="岗位排序">{{ compareData.history2.postSort }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ compareData.history2.status === '0' ? '正常' : '停用' }}</el-descriptions-item>
            <el-descriptions-item label="操作时间">{{ compareData.history2.operTime }}</el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
      <el-divider content-position="left">差异对比</el-divider>
      <el-table :data="compareData.diff" border v-if="compareData.diff && compareData.diff.length > 0">
        <el-table-column label="字段" align="center" prop="fieldLabel" />
        <el-table-column label="版本1值" align="center" prop="oldValue" />
        <el-table-column label="版本2值" align="center" prop="newValue" />
      </el-table>
      <el-empty description="无差异" v-else />
      <div slot="footer" class="dialog-footer">
        <el-button @click="compareOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPostHistory, getChangeDetails, compareHistory, rollbackPost } from '@/api/system/postHistory'

export default {
  name: 'PostHistory',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      historyList: [],
      selectedRows: [],
      dateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        postId: undefined,
        postName: undefined,
        changeType: undefined
      },
      detailOpen: false,
      currentHistory: null,
      changeDetails: [],
      compareOpen: false,
      compareData: {}
    }
  },
  computed: {
    compareDisabled() {
      return this.selectedRows.length !== 2
    }
  },
  created() {
    const postId = this.$route.query.postId
    if (postId) {
      this.queryParams.postId = postId
    }
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      const params = this.addDateRange(this.queryParams, this.dateRange, 'OperTime')
      listPostHistory(params).then(response => {
        this.historyList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.selectedRows = selection
    },
    getChangeTypeLabel(type) {
      const map = { '1': '新增', '2': '修改', '3': '删除' }
      return map[type] || '未知'
    },
    getChangeTypeTag(type) {
      const map = { '1': 'success', '2': 'warning', '3': 'danger' }
      return map[type] || 'info'
    },
    handleView(row) {
      this.currentHistory = row
      getChangeDetails(row.historyId).then(response => {
        this.changeDetails = response.data
        this.detailOpen = true
      })
    },
    handleCompare() {
      if (this.selectedRows.length !== 2) {
        this.$modal.msgWarning('请选择两个版本进行对比')
        return
      }
      const id1 = this.selectedRows[0].historyId
      const id2 = this.selectedRows[1].historyId
      compareHistory(id1, id2).then(response => {
        this.compareData = response.data
        this.compareOpen = true
      })
    },
    handleRollback(row) {
      this.$modal.confirm('确认要回滚到该版本吗？当前岗位信息将被覆盖。').then(() => {
        return rollbackPost(row.historyId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('回滚成功')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.text-danger {
  color: #f56c6c;
  text-decoration: line-through;
}
.text-success {
  color: #67c23a;
  font-weight: bold;
}
</style>
