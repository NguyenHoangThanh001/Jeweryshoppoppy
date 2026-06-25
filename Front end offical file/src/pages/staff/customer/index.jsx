import {
  Button,
  Col,
  Form,
  Input,
  Modal,
  Popconfirm,
  Row,
  Select,
  Table,
  Tag,
} from "antd";
import { useEffect, useState } from "react";
import api from "../../../config/axios";
import { toast } from "react-toastify";
import { QuestionCircleOutlined } from "@ant-design/icons";
import "./index.scss";
function ManageCustomer({ handleSelectCustomer }) {
  // const [isShowModal, setShowModal] = useState(false);
  const [showModal, setShowModal] = useState(-2);
  const [form] = Form.useForm();
  const [account, setAccount] = useState([]);
  const [loading, setLoading] = useState(true);
  const [keyWord, setKeyWord] = useState("");
  const onFinish = async (values) => {
    try {
      setLoading(true);
      console.log("Form values:", values);
      console.log(showModal);
      if (showModal >= 0) {
        const response = await api.put(
          `/account/${account[showModal].id}`,
          values
        );
        account[showModal] = response.data;
        setAccount([...account]);
      } else {
        await api.post("/auth/register", {
          ...values,
          role: "CUSTOMER",
        });
        toast.success("Successfully create new account");
        setAccount([
          ...account,
          {
            ...values,
            role: "CUSTOMER",
          },
        ]);
      }
      // Handle form submission here
      form.resetFields();
      setShowModal(-2);
      setLoading(false);
    } catch (err) {
      console.log(err);
      toast.error(err.response.data);
      setLoading(false);
    }
  };

  const fetchAccount = async () => {
    const response = await api.get("account?role=CUSTOMER");
    setAccount(response.data);
    setLoading(false);
  };

  useEffect(() => {
    fetchAccount();
  }, []);

  useEffect(() => {
    if (showModal >= 0) {
      form.setFieldsValue(account[showModal]);
      console.log(account[showModal]);
    } else {
      form.resetFields();
    }
  }, [showModal]);

  const columns = [
    {
      title: "Phone",
      dataIndex: "phone",
      key: "phone",
    },
    {
      title: "Full Name",
      dataIndex: "fullName",
      key: "fullName",
    },
    {
      title: "Email",
      dataIndex: "email",
      key: "email",
    },
    {
      title: "Điểm",
      dataIndex: "point",
      key: "point",
    },
    {
      title: "status",
      dataIndex: "accountStatus",
      key: "accountStatus",
      render: (status) => (
        <Tag color={status === "DELETED" ? "red" : "green"}>
          {status ?? "ACTIVE"}
        </Tag>
      ),
    },
    {
      title: "Role",
      dataIndex: "role",
      key: "role",
      render: (role) => (
        <Tag color={role === "ADMIN" ? "red" : "blue"}>
          {role?.toUpperCase()}
        </Tag>
      ),
    },
    {
      title: "Action",
      dataIndex: "id",
      key: "action",
      render: (value, record, index) => (
        <Row>
          <Button
            type="primary"
            onClick={() => {
              console.log(index);
              setShowModal(index);
            }}
          >
            Sửa
          </Button>
          {record.accountStatus !== "DELETED" && record.role !== "ADMIN" && (
            <>
              <Popconfirm
                placement="rightBottom"
                title="Delete the account"
                description="Are you sure to delete this account?"
                icon={<QuestionCircleOutlined style={{ color: "red" }} />}
                onConfirm={async () => {
                  await api.delete(`/account/${value}`).then(() => {
                    toast.success("Account deleted");
                    account[index].accountStatus = "DELETED";
                    setAccount([...account]);
                  });
                }}
              >
                <Button
                  style={{
                    marginLeft: 10,
                  }}
                  danger
                  type="primary"
                >
                  Xoá
                </Button>
              </Popconfirm>
              <Button
                type="primary"
                style={{
                  marginLeft: 10,
                }}
                onClick={() => {
                  if (record.accountStatus === "DELETED") {
                    toast.error("Account deleted!");
                  } else {
                    handleSelectCustomer(record);
                  }
                }}
              >
                Chọn
              </Button>
            </>
          )}
        </Row>
      ),
    },
  ];
  return (
    <div>
      <Row>
        <Col span={12}>
          <Button
            onClick={() => {
              setShowModal(-1);
            }}
            type="primary"
            style={{
              marginBottom: 20,
            }}
          >
            Create new account
          </Button>
        </Col>
        <Col span={12}>
          <Row gutter={12}>
            <Col span={20}>
              <Input
                value={keyWord}
                onChange={(e) => setKeyWord(e.target.value)}
                placeholder="Phone, Email or Full Name"
                width={100}
              />
            </Col>
            <Col span={4}>
              <Button
                type="primary"
                onClick={async () => {
                  setLoading(true);
                  const response = await api.get(`account?keyWord=${keyWord}`);
                  setAccount(response.data);
                  setLoading(false);
                }}
              >
                Search
              </Button>
            </Col>
          </Row>
        </Col>
      </Row>
      <Table
        pagination={{
          pageSize: 5,
        }}
        dataSource={account}
        columns={columns}
        loading={loading}
      />
      <Modal
        confirmLoading={loading}
        open={showModal !== -2}
        title="Create new account"
        onCancel={() => setShowModal(-2)}
        onOk={() => {
          form.submit();
        }}
      >
        <Form form={form} name="userForm" onFinish={onFinish} layout="vertical">
          <Form.Item
            name="phone"
            label="Số điện thoại"
            rules={[
              { required: true, message: "Vui lòng nhập số điện thoại!" },
              {
                pattern:
                  /^(?:\+84|0)(?:3[2-9]|5[6|8|9]|7[0|6-9]|8[1-5]|9[0-9])[0-9]{7}$/,
                message: "Số điện thoại không hợp lệ!",
              },
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="fullName"
            label="Họ và tên"
            rules={[{ required: true, message: "Vui lòng nhập họ và tên!" }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="email"
            label="Email"
            rules={[
              { required: true, message: "Vui lòng nhập email!" },
              { type: "email", message: "Vui lòng nhập email hợp lệ!" },
            ]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

export default ManageCustomer;
