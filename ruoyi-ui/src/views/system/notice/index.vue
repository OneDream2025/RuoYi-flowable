<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="公告标题" prop="noticeTitle">
        <el-input
          v-model="queryParams.noticeTitle"
          placeholder="请输入公告标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作人员" prop="createBy">
        <el-input
          v-model="queryParams.createBy"
          placeholder="请输入操作人员"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="noticeType">
        <el-select v-model="queryParams.noticeType" placeholder="公告类型" clearable>
          <el-option
            v-for="dict in dict.type.sys_notice_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="发布状态" prop="publishStatus">
        <el-select v-model="queryParams.publishStatus" placeholder="发布状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_notice_publish_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:notice:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:notice:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:notice:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="noticeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" prop="noticeId" width="80" />
      <el-table-column
        label="公告标题"
        align="center"
        prop="noticeTitle"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="公告类型" align="center" prop="noticeType" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_notice_type" :value="scope.row.noticeType"/>
        </template>
      </el-table-column>
      <el-table-column label="发布状态" align="center" prop="publishStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_notice_publish_status" :value="scope.row.publishStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_notice_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" align="center" prop="publishTime" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.publishTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建者" align="center" prop="createBy" width="80" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="250">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:notice:edit']"
          >修改</el-button>
          <el-button
            v-if="scope.row.publishStatus === '0' || scope.row.publishStatus === '1'"
            size="mini"
            type="text"
            icon="el-icon-s-promotion"
            @click="handlePublish(scope.row)"
            v-hasPermi="['system:notice:edit']"
          >发布</el-button>
          <el-button
            v-if="scope.row.publishStatus === '2' || scope.row.publishStatus === '1'"
            size="mini"
            type="text"
            icon="el-icon-circle-close"
            @click="handleCancel(scope.row)"
            v-hasPermi="['system:notice:edit']"
          >取消发布</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:notice:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改公告对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="公告标题" prop="noticeTitle">
              <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公告类型" prop="noticeType">
              <el-select v-model="form.noticeType" placeholder="请选择公告类型" style="width: 100%">
                <el-option
                  v-for="dict in dict.type.sys_notice_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布时间" prop="publishTime">
              <el-date-picker
                v-model="form.publishTime"
                type="datetime"
                placeholder="选择发布时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 100%"
                :picker-options="publishTimePickerOptions"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="form.endTime"
                type="datetime"
                placeholder="选择结束时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 100%"
                :picker-options="endTimePickerOptions"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="内容">
              <editor v-model="form.noticeContent" :min-height="192"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm(false)">保存草稿</el-button>
        <el-button type="success" @click="submitForm(true)">立即发布</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listNotice, getNotice, delNotice, addNotice, updateNotice, publishNotice, cancelPublishNotice } from "@/api/system/notice";

export default {
  name: "Notice",
  dicts: ['sys_notice_status', 'sys_notice_type', 'sys_notice_publish_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 公告表格数据
      noticeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeTitle: undefined,
        createBy: undefined,
        noticeType: undefined,
        publishStatus: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        noticeTitle: [
          { required: true, message: "公告标题不能为空", trigger: "blur" }
        ],
        noticeType: [
          { required: true, message: "公告类型不能为空", trigger: "change" }
        ],
        endTime: [
          { validator: this.validateEndTime, trigger: "change" }
        ]
      },
      // 发布时间选择器配置
      publishTimePickerOptions: {
        disabledDate: (time) => {
          // 禁止选择过去的时间（可选，根据需求调整）
          // return time.getTime() < Date.now() - 8.64e7;
          return false;
        }
      },
      // 结束时间选择器配置
      endTimePickerOptions: {
        disabledDate: (time) => {
          // 结束时间不能早于当前时间
          return time.getTime() < Date.now() - 8.64e7;
        }
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询公告列表 */
    getList() {
      this.loading = true;
      listNotice(this.queryParams).then(response => {
        this.noticeList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        noticeId: undefined,
        noticeTitle: undefined,
        noticeType: undefined,
        noticeContent: undefined,
        status: "1",
        publishTime: undefined,
        endTime: undefined,
        publishStatus: "0",
        remark: undefined
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.noticeId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加公告";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const noticeId = row.noticeId || this.ids
      getNotice(noticeId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改公告";
      });
    },
    /** 结束时间验证 */
    validateEndTime(rule, value, callback) {
      if (value && this.form.publishTime) {
        const publishTime = new Date(this.form.publishTime);
        const endTime = new Date(value);
        if (endTime <= publishTime) {
          callback(new Error("结束时间必须晚于发布时间"));
        } else {
          callback();
        }
      } else {
        callback();
      }
    },
    /** 提交按钮 */
    submitForm: function(immediatePublish) {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (immediatePublish) {
            // 立即发布或定时发布
            this.handlePublishSubmit();
          } else {
            // 保存为草稿
            this.handleSaveDraft();
          }
        }
      });
    },
    /** 保存草稿 */
    handleSaveDraft() {
      this.form.publishStatus = "0"; // 草稿状态
      this.form.status = "1"; // 关闭状态
      if (this.form.noticeId != undefined) {
        updateNotice(this.form).then(response => {
          this.$modal.msgSuccess("保存草稿成功");
          this.open = false;
          this.getList();
        });
      } else {
        addNotice(this.form).then(response => {
          this.$modal.msgSuccess("保存草稿成功");
          this.open = false;
          this.getList();
        });
      }
    },
    /** 发布提交 */
    handlePublishSubmit() {
      publishNotice(this.form).then(response => {
        this.$modal.msgSuccess("发布成功");
        this.open = false;
        this.getList();
      });
    },
    /** 发布按钮操作 */
    handlePublish(row) {
      this.reset();
      const noticeId = row.noticeId;
      getNotice(noticeId).then(response => {
        this.form = response.data;
        // 如果没有设置发布时间，默认立即发布
        if (!this.form.publishTime) {
          const now = new Date();
          this.form.publishTime = this.parseTime(now, '{y}-{m}-{d} {h}:{i}:{s}');
        }
        this.handlePublishSubmit();
      });
    },
    /** 取消发布按钮操作 */
    handleCancel(row) {
      const noticeId = row.noticeId;
      this.$modal.confirm('是否确认取消发布该公告？').then(function() {
        return cancelPublishNotice(noticeId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("取消发布成功");
      }).catch(() => {});
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const noticeIds = row.noticeId || this.ids
      this.$modal.confirm('是否确认删除公告编号为"' + noticeIds + '"的数据项？').then(function() {
        return delNotice(noticeIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>
