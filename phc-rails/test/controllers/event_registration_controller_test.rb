require 'test_helper'

class EventRegistrationControllerTest < ActionController::TestCase
  test "should get create" do
    get :create
    assert_response :success
  end

  test "should get search" do
    get :search
    assert_response :success
  end

  test "should get update_service" do
    get :update_service
    assert_response :success
  end

  test "should get update_feedback" do
    get :update_feedback
    assert_response :success
  end

end
