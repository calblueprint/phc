class Api::V1::EventRegistrationsController < ApplicationController
  skip_before_action :verify_authenticity_token
  before_action :verify_security_token

  def create
    event_reg = EventRegistration.new

    byebug
    # Create account if salesforce id was not passed in
    sf_id = params[:account_sfid]
    if (sf_id.nil? || sf_id.empty?)
      params.delete :account_sfid
      params[:sf_id] = ""
      account = Account.spawn(params)
    else
      # We retrieve the corresponding account if the salesforce id was passed in
      if not Account.exists?(sf_id: sf_id)
        raise "Unknown Salesforce ID. This should not happen!"
      end
      account = Account.find_by(sf_id: params[:account_sfid])
    end
    # Event Registrations map to an account based on account id
    event_reg[:account_id] = account.id

    # Number__c is the QR_code that was passed in, which identifies a
    # particular account for the current PHC event
    event_reg.Number__c = params[:Number__c]
    Service.services.each do |service|
      service = Service.create(name:service)
      if params[service] == true then service.applied! end
      event_reg.services << service
    end
    status = event_reg.save ? "Success" : "Failure"
    api_message_response(200, status)
  end

  def search
    qr_code = request.headers["HTTP_NUMBER__C"]
    puts qr_code
    render json: { present: (EventRegistration.exists?(Number__c: qr_code) ? true : false) }
  end

  def get_applied
    event_registration = EventRegistration.find_by(Number__c: params[:Number__c])
    if !event_registration.nil?
      applied_services = []
      event_registration.services.each do |s|
        if s.applied?
          applied_services << s.name
        end
      end
      render json: { status: "true", services: applied_services }
    else
      api_message_response(404, "Event registration with that number does not exist.")
    end
  end

  def update_service
    event_registration = EventRegistration.find_by(Number__c: params[:Number__c])
    if event_registration.nil?
      api_message_response(404, "Event registration with that number does not exist.")
      return
    end

    service = registration.services.find_by(name: params[:service_name])
    if service.nil?
      api_message_response(404, "Service with that name does not exist.")
      return
    end

    case service.status
    when "unspecified"
      service.drop_in!
      api_message_response(200, "Client's status set to drop-in.")
    when "applied"
      service.received!
      api_message_response(200, "Client's status set to received.")
    when "drop_in"
      api_message_response(200, "Client has already received service.")
    when "received"
      api_message_response(200, "Client has already received service.")
    else
      api_message_response(400, "Invalid status.")
    end
  end

  def update_feedback
    event_registration = EventRegistration.find_by(Number__c: params[:Number__c])
    if event_registration.update(event_registration_params)
      api_message_response(201, "Successfully recieved feedback!")
    else
      api_message_response(404, "Event registration with that number does not exist.")
    end
  end

  private

  def event_registration_params
    params.permit(:Number__c, :Experience__c, :Services_Needed__c, :Feedback__c)
  end

end
