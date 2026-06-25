import { Button, Form, Input, Row } from "antd";
import AuthTemplate from "../../component/auth-template";
import api from "../../config/axios";
import { toast } from "react-toastify";

function ForgotPassword() {
  const onFinish = async (values) => {
    try {
      await api.post("/auth/forgot-password", values);
      toast.success("Please check your email!");
    } catch (err) {
      toast.error(err.response.data);
    }
  };
  return (
    <AuthTemplate>
      <Form
        onFinish={onFinish}
        labelCol={{
          span: 24,
        }}
      >
        <Form.Item
          label="Email"
          name="email"
          rules={[
            {
              required: true,
              message: "Please input your email!",
            },
          ]}
        >
          <Input />
        </Form.Item>
        <Row justify={"center"}>
          <Button htmlType="submit" type="primary">
            Forgot password
          </Button>
        </Row>
      </Form>
    </AuthTemplate>
  );
}

export default ForgotPassword;
